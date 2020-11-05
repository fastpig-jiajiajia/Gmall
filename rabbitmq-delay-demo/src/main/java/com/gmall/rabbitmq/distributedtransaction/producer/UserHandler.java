package com.gmall.rabbitmq.distributedtransaction.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gmall.rabbitmq.entity.User;
import com.gmall.rabbitmq.service.UserService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.impl.AMQChannel;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
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
@Component
public class UserHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    private final static String exchangeName = "GMALL_DISTRIBUTE_TRANSACTION_EXCHANGE";
    private final static String queueName = "GMALL_DISTRIBUTE_TRANSACTION_QUEUE";
    private final static String routeKey = "gmall.distribute.transaction";


    @Transactional(rollbackFor = Exception.class)
    public String handlerUser(){
        // 插入數據
        User user = new User();
        user.setUserName("1105");
        user.setPassWord("1105PassWord");
        user.setRealName("1105RealName");
        userService.insertUser(user);

        // 向数据库中插入一条消息数据，初始状态为 0，代表消息未发送成功

        // 发送消息
        if(publishMessage(user)){

        }

        return null;
    }


    /**
     * 发送消息
     * @param user
     * @return
     */
    private boolean publishMessage(User user){
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
            rabbitTemplate.convertAndSend(exchangeName, routeKey,  JSONObject.toJSONString(messageMap), correlationData);
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
     * @param user
     * @return
     */
    private ConfirmListener getConfirmListener(User user){
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

}
