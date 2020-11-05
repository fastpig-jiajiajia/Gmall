package com.gmall.rabbitmq.consumer;

import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Component
public class DelayConsumerRabbitMQ {

    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "GMALL-DLX-EXCHANGE", durable = "true", type = "topic"),
            value = @Queue("GMALL-DLX-QUEUE"),  key = "gamll.dlx"))
    public void mergeCart(Message message) {
        byte[] bytes = message.getBody();
        String msg = new String(bytes);
        try {
            System.out.println(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
