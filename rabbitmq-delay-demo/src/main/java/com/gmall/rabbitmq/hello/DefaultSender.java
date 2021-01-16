package com.gmall.rabbitmq.hello;

import com.gmall.rabbitmq.MQConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**

 *类说明：
 */
@Component
public class DefaultSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String msg) {
        String sendMsg = msg +"---"+ System.currentTimeMillis();;
        System.out.println("Sender : " + sendMsg);
        //TODO 普通消息处理
        //this.rabbitTemplate.convertAndSend(RmConst.QUEUE_HELLO, sendMsg);
        //TODO 消息处理--(消费者处理时，有手动应答)
        this.rabbitTemplate.convertAndSend(MQConstant.QUEUE_USER, sendMsg);
    }

}
