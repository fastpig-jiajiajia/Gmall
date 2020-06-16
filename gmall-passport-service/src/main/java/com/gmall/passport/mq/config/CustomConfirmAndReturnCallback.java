package com.gmall.passport.mq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * @Description 自定义消息发送确认的回调
 *
 * 实现接口：implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback
 * ConfirmCallback：只确认消息是否正确到达交换机中,不管是否到达交换机,该回调都会执行;
 * ReturnCallback：如果消息从交换机未正确到达队列中将会执行，正确到达则不执行;
 */
@Component
public class CustomConfirmAndReturnCallback implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    private static final Logger logger = LoggerFactory.getLogger(CustomConfirmAndReturnCallback.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * PostConstruct: 用于在依赖关系注入完成之后需要执行的方法上，以执行任何初始化.
     */
    @PostConstruct
    public void init() {
        //指定 ConfirmCallback
        rabbitTemplate.setConfirmCallback(this);
        //指定 ReturnCallback
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 消息从交换机成功到达队列，则returnedMessage方法不会执行;
     * 消息从交换机未能成功到达队列，则returnedMessage方法会执行;
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        logger.info("returnedMessage回调方法>>>" + new String(message.getBody(), StandardCharsets.UTF_8) + ",replyCode:" + replyCode
                + ",replyText:" + replyText + ",exchange:" + exchange + ",routingKey:" + routingKey);
    }

    /**
     * 如果消息没有到达交换机,则该方法中isSendSuccess = false,error为错误信息;
     * 如果消息正确到达交换机,则该方法中isSendSuccess = true;
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean isSendSuccess, String error) {
        logger.info("confirm回调方法>>>回调消息ID为: " + correlationData.getId());
        if (isSendSuccess) {
            logger.info("confirm回调方法>>>消息发送到交换机成功！");
        } else {
            logger.info("confirm回调方法>>>消息发送到交换机失败！，原因 : [{}]", error);
        }
    }

}
