package com.gmall.rabbitmq.producer;

import com.rabbitmq.client.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DelayRabbitMQ {

    private final static String HOST = "39.101.198.56";
    private final static int PORT = 5672;
    private final static String USERNAME = "guest";
    private final static String PASSWORD = "guest";
    private final static String VIRTUALHOST = "/gmall";

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
     * 第二种方案，通过TTl + DLX (设置过期时间，超过时间未消费被转移到死信队列 实现延迟队列
     *
     * 将消息投入到设置超时间的 ttl queue 当中，
     * 这个queue 除了自己的交换机，还绑定了 dlx 死信队列的交换机，用于将超时未消费的消息投入到 dlx 死信队列中.
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

            // 创建DLX及死信队列，用于接收超期的消息
            String dlxExchageName = "GMALL-DLX-EXCHANGE";
            String dlxQueueName = "GMALL-DLX-QUEUE";
            String dlxRouteKey = "gmall.dlx";
            channel.exchangeDeclare(dlxExchageName, "topic", true);
            channel.queueDeclare(dlxQueueName, true, false, false, null);
            channel.queueBind(dlxQueueName, dlxExchageName, dlxRouteKey);


            // 创建 ttl 存放消息的队列
            String ttlExchangeName = "GMALL-TTL-EXCHANGE";
            String ttlQueueName = "GMALL-TTL-QUEUE";
            String ttlRouteKey = "gmall.ttl";
            channel.exchangeDeclare(ttlExchangeName, "topic", true);
            Map<String, Object> arguments = new HashMap<>();
            //过期时间10s
            arguments.put("x-message-ttl", 10 * 1000);
            //绑定DLX
            arguments.put("x-dead-letter-exchange", dlxExchageName);
            //绑定发送到DLX的RoutingKey
            arguments.put("x-dead-letter-routing-key", dlxRouteKey);
            channel.queueDeclare(ttlQueueName, true, false, false, arguments);
            channel.queueBind(ttlQueueName, ttlExchangeName, ttlRouteKey);
            //发布一条消息
            channel.basicPublish(ttlExchangeName, ttlRouteKey, null, "ttl + dlx 死信队列 10 s".getBytes());

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


}
