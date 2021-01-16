package com.gmall.rabbitmq.delay.distributedtransaction.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gmall.rabbitmq.MQConstant;
import com.gmall.rabbitmq.delay.entity.User;
import com.gmall.rabbitmq.delay.service.UserService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author rui.xu
 * @date 2020/11/05 16:51
 * @description
 **/
@Slf4j
@Component
public class UserHandler implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    private final static String queueName = MQConstant.TRANSACTION_QUEUE;
    private final static String exchangeName = MQConstant.TRANSACTION_EXCHANGE_TOPIC;
    private final static String routeKey = MQConstant.TRANSACTION_EXCHANGE_KEY;


    @Transactional(rollbackFor = Exception.class)
    public String handlerUser() {
        // 插入数据
        User user = new User();
        user.setUserName("1105");
        user.setPassWord("1105PassWord");
        user.setRealName("1105RealName");
        userService.insertUser(user);

        // 向数据库中插入一条消息数据，初始状态为 0，代表消息未发送成功

        // 发送消息
        if (publishMessage(user)) {

        }

        return null;
    }


    /**
     * 发送消息
     *
     * @param user
     * @return
     */
    private boolean publishMessage(User user) {
        Boolean isSendSuccess = false;
        // 定义信道
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.createConnection();
            channel = connection.getDelegate().createChannel();

            String messageId = UUID.randomUUID().toString();
            String createTime = org.apache.http.client.utils.DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("messageId", messageId);
            messageMap.put("outTradeNo", "orderSN" + (new Date()).getTime());
            messageMap.put("count", (new Date()).getTime());
            messageMap.put("createTime", createTime);
            messageMap.put("userInfo", JSON.toJSONString(user));

            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            channel.basicPublish(exchangeName, queueName, builder.build(), JSONObject.toJSONString(messageMap).getBytes());
            CorrelationData correlationData = new CorrelationData();
            correlationData.setId(UUID.randomUUID().toString());
            rabbitTemplate.convertAndSend(exchangeName, routeKey, JSONObject.toJSONString(messageMap), correlationData);
            rabbitTemplate.convertAndSend(exchangeName, routeKey, JSONObject.toJSONString(messageMap), new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);//设置消息持久化
                    return message;
                }
            }, correlationData);
//            channel.basicPublish(exchange_name, "gmall.#", null, JSONObject.toJSONString(messageMap).getBytes());

            // 开启发送方确认模式
            channel.confirmSelect();
            // 异步监听确认和未确认的消息
            channel.addConfirmListener(getConfirmListener(user));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return isSendSuccess;
    }


    /**
     * 异步监听确认和未确认的消息
     *
     * @param user
     * @return
     */
    private ConfirmListener getConfirmListener(User user) {
        ConfirmListener listener = new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("未确认消息，标识：" + deliveryTag);
                // 更新数据库 retry 字段 +1，代表该消息重试的次数
                publishMessage(user);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println(String.format("已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple));
                // 更新数据库 messagePushConfirm 表 status 状态为 1
            }
        };

        return listener;
    }


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if(!ack){
            /*
             *   处理消息没有到达交换机，数据丢失的情况
             *   根据订单号查询到订单数据，并将数据保存到异常消息表中，定时补发，并报警人工处理
             * */
            String orderId = correlationData.getId();
        }else{
            //查询订单号是否在异常消息表，在的话要删除
            log.info(">>>下单消息发送成功{}<<<",correlationData);

        }
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        //消息到达交换机，没有路由到队列，根据订单号查询到订单数据，并将数据保存到异常消息表中，定时补发，并报警人工处理
        /*
         *  1 交换机没有绑定队列
         *  2 交换机根据路由键没有匹配到队列
         *  3 队列消息已满
         * */
        byte[] body = message.getBody();
        JSONObject json = JSONObject.parseObject((new String(body)));
        System.out.println("return============================");
        System.out.println(message);
    }
}
