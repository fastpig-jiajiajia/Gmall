package com.gmall.rabbitmq.delay.consumer;

import com.gmall.rabbitmq.MQConstant;
import com.rabbitmq.client.Channel;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Component
public class DelayConsumerRabbitMQ {


    @RabbitHandler
    //通过配置文件发送消息
//    @RabbitListener(queues ={"${zhihao.queue1}","${zhihao.queue2}"})
    //支持自动声明绑定，声明之后自动监听队列的队列，此时@RabbitListener注解的queue和bindings不能同时指定，否则报错
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MQConstant.DLX_EXCHANGE_TOPIC, durable = "true", type = "topic"),
            value = @Queue(MQConstant.DLX_QUEUE),  key = MQConstant.DLX_EXCHANGE_KEY))
    public void mergeCart(Message message, Channel channel) {
        byte[] bytes = message.getBody();
        String msg = new String(bytes);
        try {
            System.out.println(msg);
            // 确认收到消息，只确认当前消费者的一个消息收到
            // 消息消费完手动确认消息，确认完消息才会从内存(和磁盘，如果是持久化消息的话)中移去消息
            // 第一个参数：deliveryTag 表示该消息的index（long类型的数字）；
            //第二个参数： multiple 表示是否批量（true：将一次性ack所有小于deliveryTag的消息）；
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            try {
                //TODO 消息重发
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                        false,true);
                //TODO Reject方式拒绝(这里第2个参数决定是否重新投递,true 重新投递，false 丢弃消息)
                //channel.basicReject(envelope.getDeliveryTag(),true);

                //TODO Nack方式的拒绝（第2个参数决定是否批量，无法进行重新投递）
//                channel.basicNack(envelope.getDeliveryTag(), false, true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.println("UserReceiver>>>>>>拒绝消息，要求Mq重新派发");
            e.printStackTrace();
        }
    }

}
