package com.gmall.rabbitmq.topic;

import com.gmall.rabbitmq.MQConstant;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.ReturnListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**

 *类说明：
 */
@Component
public class TopicSender {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send() {
        String msg1 = "I am email mesaage msg======";
        System.out.println("TopicSender send the 1st : " + msg1);
        this.rabbitTemplate.convertAndSend(MQConstant.EXCHANGE_TOPIC, MQConstant.RK_EMAIL, msg1);

        String msg2 = "I am user mesaages msg########";
        System.out.println("TopicSender send the 2nd : " + msg2);
        this.rabbitTemplate.convertAndSend(MQConstant.EXCHANGE_TOPIC, MQConstant.RK_USER, msg2);

        String msg3 = "I am error mesaages msg";
        System.out.println("TopicSender send the 3rd : " + msg3);
        this.rabbitTemplate.convertAndSend(MQConstant.EXCHANGE_TOPIC, "errorkey", msg3);

        // 添加发送者确认监听器

    }

}
