package com.gmall.cart.mq;


import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.service.CartService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Consumer 消费者接受消息
 *
 * @author xurui
 *
 */
@Component
public class CartRabbitMQConsumer {

    @Reference
    private CartService cartService;

    @RabbitListener(queues = "LOGIN_MERGECART_QUEUE")
    @RabbitHandler
    public void rabbitMQConsumer(Message message, Channel channel){
        try{
            byte[] bytes = message.getBody();
            System.out.println(new String(bytes));
            cartService.mergeCart(null, null);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
