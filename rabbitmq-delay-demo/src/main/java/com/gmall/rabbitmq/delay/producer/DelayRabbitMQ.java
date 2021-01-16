package com.gmall.rabbitmq.delay.producer;

import com.gmall.rabbitmq.MQConstant;
import com.rabbitmq.client.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DelayRabbitMQ {

    private final static String HOST = "39.101.198.56";
    private final static int PORT = 5672;
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "admin";
    private final static String VIRTUALHOST = "my_vhost";

    public static void main(String[] args) {
    //    delayQueueByPlugins();
        delayQueueByTTL();
    }


    /**
     * 第一种方案，通过延时插件实现延迟队列
     */
    private static void delayQueueByPlugins(){
        Connection connection = null;
        Channel channel = null;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setPort(PORT);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);
            factory.setVirtualHost(VIRTUALHOST);
            connection = factory.newConnection();

            // 定义信道
            String exchangeName = "GMALL_PAYMENT_DELAY_EXCHANGE";
            String routeKey = "gmall.delay";
            channel = connection.createChannel();

            // 定义交换机和队列信息
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-delayed-type", "direct");
            CorrelationData correlationData = new CorrelationData();
            correlationData.setId(UUID.randomUUID().toString());
            // 定义延时队列
            channel.exchangeDeclare(exchangeName, "x-delayed-message", true, false, arguments);

            Map<String, Object> headers = new HashMap<>();
            //延迟10s后发送
            headers.put("x-delay", 20* 1000);
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.headers(headers);
            channel.basicPublish(exchangeName, routeKey, builder.build(), "延时队列20s".getBytes());

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(channel != null){
                    channel.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }

        }
    }


    /**
     * 第二种方案，通过TTL + DLX (设置过期时间，超过时间未消费被转移到死信队列 实现延迟队列)
     *
     * 将消息投入到设置超时间的 ttl queue 当中，
     * TTL 队列没有消费者，过了超时时间就会将消息发送到绑定的 DLX 队列中进行消费，实现延迟队列功能
     * 这个queue 除了自己的交换机，还绑定了 DLX 死信队列的交换机，用于将超时未消费的消息投入到 DLX 死信队列中.
     */
    private static void delayQueueByTTL(){
        Connection connection = null;
        Channel channel = null;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setPort(PORT);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);
            factory.setVirtualHost(VIRTUALHOST);
            connection = factory.newConnection();
            channel = connection.createChannel();

//            // 创建DLX及死信队列，用于接收超期的消息
//            createDlxChannel(channel);
//            // 创建 ttl 存放消息的队列
//            createTtlChannel(channel);

            //发布一条消息
            channel.basicPublish(MQConstant.TTL_EXCHANGE_TOPIC, MQConstant.TTL_EXCHANGE_KEY, null, "ttl + dlx 死信队列 10 s".getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(channel != null){
                    channel.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }

        }
    }

    /**
     * 创建DLX及死信队列，用于接收超期的消息
     * @param channel
     * @return
     * @throws IOException
     */
    private static Channel createDlxChannel(Channel channel) throws IOException {
        String dlxExchageName = MQConstant.DLX_EXCHANGE_TOPIC;
        String dlxQueueName = MQConstant.DLX_QUEUE;
        String dlxRouteKey = MQConstant.DLX_EXCHANGE_KEY;
        channel.exchangeDeclare(dlxExchageName, "topic", true);
        channel.queueDeclare(dlxQueueName, true, false, false, null);
        channel.queueBind(dlxQueueName, dlxExchageName, dlxRouteKey);
        return channel;
    }

    /**
     * 创建 ttl 存放消息的队列
     * @param channel
     * @return
     * @throws IOException
     */
    private static Channel createTtlChannel(Channel channel) throws IOException {
        String ttlExchangeName = MQConstant.TTL_QUEUE;
        String ttlQueueName = MQConstant.TTL_EXCHANGE_TOPIC;
        String ttlRouteKey = MQConstant.TTL_EXCHANGE_KEY;
        channel.exchangeDeclare(ttlExchangeName, "topic", true);
        Map<String, Object> arguments = new HashMap<>();
        //过期时间10s
        arguments.put("x-message-ttl", 10 * 1000);
        //绑定DLX
        arguments.put("x-dead-letter-exchange", MQConstant.DLX_EXCHANGE_TOPIC);
        //绑定发送到DLX的RoutingKey
        arguments.put("x-dead-letter-routing-key", MQConstant.DLX_EXCHANGE_KEY);
        channel.queueDeclare(ttlQueueName, true, false, false, arguments);
        channel.queueBind(ttlQueueName, ttlExchangeName, ttlRouteKey);
        return channel;
    }


}
