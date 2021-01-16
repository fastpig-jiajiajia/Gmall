package com.gmall.rabbitmq.setmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：消息的属性的控制
 */
public class ReplyToProducer {

    public final static String EXCHANGE_NAME = "replyto";
    private final static String HOST = "39.101.198.56";
    private final static int PORT = 5672;
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "admin";
    private final static String VIRTUALHOST = "my_vhost";

    public static void main(String[] args)
            throws IOException, TimeoutException {
        /* 创建连接,连接到RabbitMQ*/
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VIRTUALHOST);
        Connection connection = factory.newConnection();

        /*创建信道*/
        Channel channel = connection.createChannel();
        /*创建持久化交换器*/
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", false);

        //TODO 响应QueueName ，消费者将会把要返回的信息发送到该Queue
        String responseQueue = channel.queueDeclare().getQueue();
        //TODO 消息的唯一id
        String msgId = UUID.randomUUID().toString();
        //TODO 设置消息中的应答属性
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .replyTo(responseQueue)
                .messageId(msgId)
                .build();

        /*声明了一个消费者*/
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received[" + envelope.getRoutingKey()
                        + "]" + message);
            }
        };
        //TODO 消费者应答队列上的消息
        channel.basicConsume(responseQueue, true, consumer);

        String msg = "Hello,RabbitMq";
        //TODO 发送消息时，把响应相关属性设置进去
        channel.basicPublish(EXCHANGE_NAME, "error",
                properties,
                msg.getBytes());
        System.out.println("Sent error:" + msg);
    }

}
