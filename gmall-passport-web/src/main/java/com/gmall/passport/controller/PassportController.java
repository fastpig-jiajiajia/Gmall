package com.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gmall.entity.UmsMember;
import com.gmall.service.UserService;
import com.gmall.util.ActiveMQUtil;
import com.gmall.util.CookieUtil;
import com.gmall.util.HttpclientUtil;
import com.gmall.util.JwtUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.AMQChannel;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.jms.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.CommonDataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class PassportController {

    @Reference
    private UserService userService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Value("${weibo.appKey.client_id}")
    private static String clientId = "546561297";
    @Value("${weibo.appSecret.client_secret}")
    private static String clientSecret = "b5bcf0f702e0c1e96add1a0423bca694";
    @Value("${weibo.redirect_uri}")
    private static String redirectUri = "http://passport.gmall.com:8085/vlogin";
    @Value("${weibo.grant_type}")
    private static String grantType = "authorization_code";

    /**
     * 验证 token 真伪
     * @param request
     * @return
     */
    @RequestMapping("verify")
    public String verify(String token, String currentIp, HttpServletRequest request){

        // 通过jwt校验token真假
        Map<String,String> map = new HashMap<>();

        // 能解析成功就为真
        Map<String, Object> decode = JwtUtil.decode(token, "2019gmall0105", currentIp);

        if(decode != null){
            map.put("status", "success");
            map.put("memberId", (String)decode.get("memberId"));
            map.put("nickname", (String)decode.get("nickname"));
        }else{
            map.put("status","fail");
        }

        return JSON.toJSONString(map);
    }

    /**
     * 登陆验证
     * @param umsMember
     * @param request
     * @return
     */
    @RequestMapping("login")
    public String login(UmsMember umsMember, HttpServletRequest request, HttpServletResponse response){

        String token = "";

        // 调用用户服务验证用户名和密码
        UmsMember umsMemberLogin = userService.login(umsMember);

        if(umsMemberLogin != null){
            // 登录成功

            // 生成jwt的token，并且重定向到首页，携带该token
            token = generateTokenByJWT(umsMember, request);
            String str = request.getRequestURL().toString();
            CookieUtil.setCookie(request, response, "cartListCookie", CookieUtil.getCookieValue(request, "cartListCookie", true), 60*60*2, true);
            CookieUtil.setCookie(request, response, "token", token, 60*60*2, true);
            request.setAttribute("token", token);
            // 将token存入redis一份
            userService.addUserToken(token, umsMember.getId());
            // 合并购物车
            sendLoginMergeCartMessageByRabbitMQ();
            sendLoginMergeCartMessageActiveMQ(token, 5);

        }else{
            // 登录失败
            token = "fail";
        }

        return token;
    }

    /**
     * 登录页面
     * @param ReturnUrl
     * @param map
     * @return
     */
    @RequestMapping("index")
    public ModelAndView index(String ReturnUrl, ModelMap map){
        map.put("ReturnUrl", ReturnUrl);
        return new ModelAndView("index");
    }

    /**
     * 微博登录对接
     * @param code
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("vlogin")
    public ModelAndView vlogin(String code, HttpServletRequest request, HttpServletResponse response){

        // 授权码换取access_token
        // 换取access_token
        // client_secret=f043fe09dcab7e9b90cdd7491e282a8f
        // client_id=2173054083
        String s3 = "https://api.weibo.com/oauth2/access_token?";//?client_id=187638711&client_secret=a79777bba04ac70d973ee002d27ed58c&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id", clientId);
        paramMap.put("client_secret", clientSecret);
        paramMap.put("grant_type", grantType);
        paramMap.put("redirect_uri", redirectUri);
        paramMap.put("code", code); // 授权有效期内可以使用，每新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);

        Map<String,Object> access_map = JSON.parseObject(access_token_json,Map.class);

        // access_token换取用户信息
        String uid = (String)access_map.get("uid");
        String access_token = (String)access_map.get("access_token");
        String show_user_url = "https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid;
        String user_json = HttpclientUtil.doGet(show_user_url);
        Map<String,Object> user_map = JSON.parseObject(user_json,Map.class);

        // 将用户信息保存数据库，用户类型设置为微博用户
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceType("2");
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceUid((String)user_map.get("idstr"));
        umsMember.setCity((String)user_map.get("location"));
        umsMember.setNickname((String)user_map.get("screen_name"));
        String g = "0";
        String gender = (String)user_map.get("gender");
        if(gender.equals("m")){
            g = "1";
        }
        umsMember.setGender(g);

        // 判断数据库是否已经有该用户的信息
        UmsMember umsCheck = new UmsMember();
        umsCheck.setSourceUid(umsMember.getSourceUid());
        UmsMember umsMemberCheck = userService.checkOauthUser(umsCheck);

        if(umsMemberCheck == null){
            userService.addOauthUser(umsMember);
        }else{
            umsMember = umsMemberCheck;
        }

        // 生成jwt的token，并且重定向到首页，携带该token
        String token = generateTokenByJWT(umsMember, request);


        // 将token存入redis一份
        userService.addUserToken(token, umsMember.getId());
        CookieUtil.setCookie(request, response, "cartListCookie", CookieUtil.getCookieValue(request, "cartListCookie", true), 60*60*2, true);
        CookieUtil.setCookie(request, response, "token", token, 60*60*2, true);

        // 合并购物车
        sendLoginMergeCartMessageByRabbitMQ();
        sendLoginMergeCartMessageActiveMQ(token, 5);
        return new ModelAndView("redirect:http://search.gmall.com:8083/index?token="+token);
    }

    /**
     * 单点登出
     */
    @RequestMapping("logout")
    public ModelAndView singleLogout(HttpServletRequest request, HttpServletResponse response){
        request.setAttribute("token", "");
        CookieUtil.deleteCookie(request, response, "token");
        return new ModelAndView("redirect:http://passport.gmall.com:8085/index");
    }

    /**
     * JWT 生成 token
     * @param umsMember
     * @param request
     * @return
     */
    private String generateTokenByJWT(UmsMember umsMember, HttpServletRequest request){
        // 生成jwt的token，并且重定向到首页，携带该token
        String token = null;
        String memberId = umsMember.getUsername();
        String nickname = umsMember.getPassword();
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("memberId", memberId);
        userMap.put("nickname", nickname);


        String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();// 从request中获取ip
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
        }

        // 按照设计的算法对参数进行加密后，生成token
        token = JwtUtil.encode("2019gmall0105", userMap, ip);

        return token;
    }

    @RequestMapping(value = "sendLoginMergeCartMessage", method = RequestMethod.POST)
    private void sendLoginMergeCartMessageByRabbitMQ(){
        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("39.101.198.56");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/gmall");
            Connection connection = factory.newConnection();
            // 定义通道
            Channel channel = connection.createChannel();
            // 定义交换机名称
            String exchange_name = "topicExchange";
            // fanout是定义发布订阅模式  direct是 路由模式 topic是主题模式
            channel.exchangeDeclare(exchange_name, "topic", true);

            String messageId = UUID.randomUUID().toString();
            String messageData = "topic message queue";
            String createTime = org.apache.http.client.utils.DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("messageId", messageId);
            messageMap.put("messageData", messageData);
            messageMap.put("createTime", createTime);

//            AMQP.Tx.CommitOk commitOk = channel.txCommit();.
//            channel.basicPublish(exchange_name, "gmall.#", null, JSONObject.toJSONString(messageMap).getBytes());
//            channel.close();
//            connection.close();

            rabbitTemplate.convertAndSend("LOGIN_MERGECART_EXCHANGE", "gmall.#", JSONObject.toJSONString(messageMap));
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * ActiveMQ 延迟队列
     * @param tradeNo
     * @param count
     */
    @RequestMapping(value = "sendLoginMergeCartMessageActiveMQ", method = RequestMethod.POST)
    public void sendLoginMergeCartMessageActiveMQ(String tradeNo, int count) {
        javax.jms.Connection connection = null;
        Session session = null;
        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try{
            // 设置队列
            Queue login_mergecart_queue = session.createQueue("LOGIN_MERGECART_QUEUE");
            MessageProducer producer = session.createProducer(login_mergecart_queue);

            //TextMessage textMessage=new ActiveMQTextMessage();//字符串文本

            MapMessage mapMessage = new ActiveMQMapMessage();// hash结构

            mapMessage.setString("tradeNo", tradeNo);
            mapMessage.setInt("count", count);

            // 为消息加入延迟时间
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*60);

            producer.send(mapMessage);

            session.commit();
        }catch (Exception ex){
            // 消息回滚
            try {
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }finally {
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }
    }
}
