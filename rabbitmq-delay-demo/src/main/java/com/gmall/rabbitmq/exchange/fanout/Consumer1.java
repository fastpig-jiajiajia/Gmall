package com.gmall.rabbitmq.exchange.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *类说明：fanout消费者--绑定多个路由键
 */
public class Consumer1 {

    public static void main(String[] argv) throws IOException,
            InterruptedException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");

        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.exchangeDeclare(FanoutProducer.EXCHANGE_NAME,
                BuiltinExchangeType.FANOUT);
        // 声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();

        /*队列绑定到交换器上时，是允许绑定多个路由键的，也就是多重绑定*/
        String[] routekeys={"king","mark","james"};
        for(String routekey:routekeys){
            channel.queueBind(queueName,FanoutProducer.EXCHANGE_NAME,
                    routekey);
        }
        System.out.println(" ["+queueName+"] Waiting for messages:");

        // 创建队列消费者
        final Consumer consumerA = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received "  + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumerA);
    }
}
