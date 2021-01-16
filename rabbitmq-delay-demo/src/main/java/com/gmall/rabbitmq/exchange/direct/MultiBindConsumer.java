package com.gmall.rabbitmq.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *类说明：队列和交换器的多重绑定
 */
public class MultiBindConsumer {

    public static void main(String[] argv) throws IOException,
            InterruptedException, TimeoutException {
        //连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //连接rabbitMq的地址
        factory.setHost("127.0.0.1");

        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        //创建一个信道
        final Channel channel = connection.createChannel();
        //信道设置交换器类型(direct)
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        //TODO
        /*队列绑定到交换器上时，是允许绑定多个路由键的，也就是多重绑定*/
        String[] routekeys={"king","mark","james"};
        for(String routekey:routekeys){
            channel.queueBind(queueName,DirectProducer.EXCHANGE_NAME,
                    routekey);
        }
        System.out.println(" [*] Waiting for messages:");

        // 创建队列消费者
        final Consumer consumerA = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties
                                               properties,
                                       byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" Received "
                        + envelope.getRoutingKey() + ":'" + message
                        + "'");
            }
        };
        channel.basicConsume(queueName, true, consumerA);
    }
}
