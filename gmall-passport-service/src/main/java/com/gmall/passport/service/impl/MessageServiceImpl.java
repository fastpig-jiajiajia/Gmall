package com.gmall.passport.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.gmall.GmallConstant;
import com.gmall.entity.MessagePushConfirm;
import com.gmall.passport.mapper.MessageMapper;
import com.gmall.service.IMessageService;
import com.rabbitmq.client.*;
import org.apache.ibatis.session.RowBounds;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 将信息插入本地消息列表，等待 job 服务扫描发送消息队列
     * @param message
     */
    @Override
    public void insertMessage(MessagePushConfirm message) {
        // 只针对有值的字段进行插入
        messageMapper.insertSelective(message);
    }

    /**
     * 查询列表信息
     */
    @Override
    public List<MessagePushConfirm> getMessageList(int status) {
        MessagePushConfirm message = new MessagePushConfirm();
        message.setStatus(status);
        List<MessagePushConfirm> messageList = messageMapper.select(message);
        return messageList;

    }


    /**
     * RabbitMQ 生产者，保证生产者的生产出来的消息，和事务保持一致性；
     * 即事务执行成功，消息必须上传MQ，事务执行失败，消息也必须不能被发送，消息可以重复发送，MQ消费端可以保证消息的幂等性
     */
    @Override
    @RequestMapping(value = "sendLoginMergeCartMessage", method = RequestMethod.POST)
    public String sendLoginMergeCartMessageByRabbitMQ(String messageData){
        Connection connection = null;
        Channel channel = null;

        try { // 创建连接
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("39.101.198.56");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/gmall");
            connection = factory.newConnection();
            // 定义信道
            channel = connection.createChannel();
            // 定义交换机名称
            String exchange_name = "LOGIN_MERGECART_EXCHANGE";
            // 声明交换机，fanout是定义发布订阅模式  direct是 路由模式 topic是主题模式
            channel.exchangeDeclare(exchange_name, "topic", true);

            String messageId = UUID.randomUUID().toString();
            String createTime = org.apache.http.client.utils.DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("messageId", messageId);
            messageMap.put("messageData", messageData);
            messageMap.put("createTime", createTime);

            CorrelationData correlationData = new CorrelationData();
            correlationData.setId(UUID.randomUUID().toString());
            rabbitTemplate.convertAndSend("LOGIN_MERGECART_EXCHANGE", "gmall.#",  JSONObject.toJSONString(messageMap), correlationData);

            // 开启发送方确认模式
            channel.confirmSelect();
//            channel.basicPublish(exchange_name, "gmall.#", null, JSONObject.toJSONString(messageMap).getBytes());
            // 异步监听确认和未确认的消息
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleNack(long deliveryTag, boolean multiple) {
                    System.out.println("未确认消息，标识：" + deliveryTag);
                }

                @Override
                public void handleAck(long deliveryTag, boolean multiple)  {
                    System.out.println(String.format("已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple));
                }
            });

            // 普通 方式监听确认
            if (channel.waitForConfirms()) {
                System.out.println("消息发送成功" );
                return GmallConstant.SUCCESS;
            }else{
                return GmallConstant.FAIL;
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            // 关闭连接
            try {
                if(channel != null){
                    channel.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return GmallConstant.FAIL;

    }

    @Override
    public void updateMessage(MessagePushConfirm messagePushConfirm) {
        messageMapper.updateByPrimaryKey(messagePushConfirm);
    }
}
