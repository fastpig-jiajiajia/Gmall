package com.gmall.rabbitmq.config;

import com.gmall.rabbitmq.MQConstant;
import com.gmall.rabbitmq.hello.UserReceiver;
import com.rabbitmq.client.impl.recovery.RecordedQueue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * 类说明：
 */
@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.username}")
    private String userName;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.port}")
    private Integer port;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${spring.rabbitmq.publisher-confirms}")
    private Boolean publisherConfirms;

    @Value("${spring.rabbitmq.publisher-returns}")
    private Boolean publisherReturns;

    @Autowired
    private UserReceiver userReceiver;

    //TODO 连接工厂
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(publisherConfirms); // 消息发送确认
        connectionFactory.setPublisherReturns(publisherReturns);  //TODO 如果要进行消息回调，则这里必须要设置为true
        return connectionFactory;
    }

    //TODO rabbitAdmin类封装对RabbitMQ的管理操作
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * 在springboot 整合rabbitmq下  rabbitTemplate 默认是单例形式
     * 如果仅是发送队列和接受队列消息 该单例模式就足够使用了
     * 如果想要 对于 发布端进行消息推送确认，那么单例模式是无法满足的
     * 如果我们有多个队列，并需要对每个队列发送是否成功的消息进行确认
     * 这种情况下，如果是单例模式，那么整个项目中，仅有个一confirm 和 returncallback 实现接口
     * 对于所有的队列消息的确认也只能在这一个接口中得到回复，这样就很难辨别确认的消息响应是具体哪个队列的。
     * 所以这样的情况下，单例是无法满足的，因此需要使用多例模式
     *
     * @return
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)  //必须是prototype类型
    public RabbitTemplate newRabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMandatory(true); // 失败通知
        template.setConfirmCallback(confirmCallback()); // 发送方确认
        template.setReturnCallback(returnCallback()); // 失败回调
        return template;
    }

    //===============使用了RabbitMQ系统缺省的交换器（direct交换器）==========
    //TODO 申明队列（最简单的方式）
    @Bean
    public Queue helloQueue() {
        return new Queue(MQConstant.QUEUE_HELLO);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(MQConstant.QUEUE_USER);
    }

    // ===============topic Exchange 开始==========
    @Bean
    public Queue queueEmailMessage() {
        return new Queue(MQConstant.QUEUE_TOPIC_EMAIL);
    }

    @Bean
    public Queue queueUserMessages() {
        return new Queue(MQConstant.QUEUE_TOPIC_USER);
    }

    //TODO 申明交换器(topic交换器)
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(MQConstant.EXCHANGE_TOPIC);
    }

    //TODO 绑定关系
    @Bean
    public Binding bindingEmailExchangeMessage() {
        return BindingBuilder
                .bind(queueEmailMessage())
                .to(exchange())
                .with("sb.*.email");
    }

    @Bean
    public Binding bindingUserExchangeMessages() {
        return BindingBuilder
                .bind(queueUserMessages())
                .to(exchange())
                .with("sb.*.user");
    }

    // =============== topic Exchange 结束==========

    // ===============Fanout Exchange 开始==========
    //TODO 申明队列
    @Bean
    public Queue AMessage() {
        return new Queue("sb.fanout.A");
    }

    //TODO 申明交换器(fanout交换器)
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(MQConstant.EXCHANGE_FANOUT);
    }

    //TODO 绑定关系
    @Bean
    Binding bindingExchangeA(Queue AMessage, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(AMessage).to(fanoutExchange);
    }

    // =============== Fanout Exchange 结束 ==========

    // =============== 消费者确认==========
    @Bean
    public SimpleMessageListenerContainer messageContainer() {
        SimpleMessageListenerContainer container
                = new SimpleMessageListenerContainer(connectionFactory());
        //TODO 绑定了这个sb.user队列
        container.setQueues(userQueue());
        //TODO 手动提交
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //TODO 消费确认方法
        container.setMessageListener(userReceiver);
        return container;
    }

    // ============= TTL+DLX 队列配置开始 =====================
    @Bean
    public Queue dlxQueue() {
        return new Queue(MQConstant.DLX_QUEUE);
    }

    @Bean
    public Queue ttlQueue() {

        Map<String, Object> arguments = new HashMap<>();
        //过期时间 10s
        arguments.put("x-message-ttl", 10 * 1000);
        //绑定 DLX
        arguments.put("x-dead-letter-exchange", MQConstant.DLX_EXCHANGE_TOPIC);
        //绑定发送到 DLX 的 RoutingKey
        arguments.put("x-dead-letter-routing-key", MQConstant.DLX_EXCHANGE_KEY);
        Queue queue = new Queue(MQConstant.TTL_QUEUE, true, false, false, arguments);
        return queue;
    }

    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange(MQConstant.DLX_EXCHANGE_TOPIC);
    }

    @Bean
    public TopicExchange ttlExchange() {
        return new TopicExchange(MQConstant.TTL_EXCHANGE_TOPIC, true, false);
    }

    @Bean
    public Binding bindingDLXExchangeMessages() {
        return BindingBuilder
                .bind(dlxQueue())
                .to(dlxExchange())
                .with(MQConstant.DLX_EXCHANGE_KEY)
                ;
    }

    @Bean
    public Binding bindingTTLExchangeMessages() {
        return BindingBuilder
                .bind(ttlQueue())
                .to(ttlExchange())
                .with(MQConstant.TTL_EXCHANGE_KEY)
                ;
    }
    // ============= TTL+DLX 队列配置结束 =====================


    // ============= 延迟队列插件队列配置开始 =====================
    @Bean
    public Queue delayPluginQueue() {
        return new Queue(MQConstant.DELAY_PLUGIN_QUEUE);
    }

    @Bean
    public TopicExchange delayPluginExchange() {
        return new TopicExchange(MQConstant.DELAY_PLUGIN_EXCHANGE_TOPIC, true, false);
    }

    @Bean
    public Binding bindingDelayPluginExchangeMessages() {
        return BindingBuilder
                .bind(delayPluginQueue())
                .to(delayPluginExchange())
                .with(MQConstant.DELAY_PLUGIN_EXCHANGE_KEY)
                ;
    }
    // ============= 延迟队列插件队列配置结束 =====================


    // ============= 分布式事务队列配置开始 =====================
    @Bean
    public Queue transactionQueue() {
        return new Queue(MQConstant.TRANSACTION_QUEUE);
    }

    @Bean
    public TopicExchange transactionExchange() {
        return new TopicExchange(MQConstant.TRANSACTION_EXCHANGE_TOPIC, true, false);
    }

    @Bean
    public Binding bindingTransactionExchangeMessages() {
        return BindingBuilder
                .bind(transactionQueue())
                .to(transactionExchange())
                .with(MQConstant.TRANSACTION_EXCHANGE_KEY)
                ;
    }
    // ============= 分布式事务队列插件队列配置结束 =====================


    /**
     * 只确认消息是否正确到达交换机中，不管是否到达交换机，该回调都会执行;
     * 如果消息没有到达交换机，则该方法中isSendSuccess = false,error为错误信息;
     * 如果消息正确到达交换机，则该方法中isSendSuccess = true;
     */
    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback() {
        return new RabbitTemplate.ConfirmCallback() {

            @Override
            public void confirm(CorrelationData correlationData,
                                boolean ack, String cause) {
                if (ack) {
                    System.out.println("发送者确认发送给mq成功");
                } else {
                    //处理失败的消息
                    System.out.println("发送者发送给mq失败,考虑重发:" + cause);
                }
            }
        };
    }

    /**
     * 消息从交换机成功到达队列，则returnedMessage方法不会执行;
     * 消息从交换机未能成功到达队列，如果设置了备用交换机，则returnedMessage方法不会执行;
     *  没有备用交换机，则returnedMessage方法会执行;
     * 只关注是否路由到，exchange 后续处理报错不会执行到。
     */
    @Bean
    public RabbitTemplate.ReturnCallback returnCallback() {
        return new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message,
                                        int replyCode,
                                        String replyText,
                                        String exchange,
                                        String routingKey) {
                System.out.println("无法路由的消息，需要考虑另外处理。");
                System.out.println("Returned replyText：" + replyText);
                System.out.println("Returned exchange：" + exchange);
                System.out.println("Returned routingKey：" + routingKey);
                String msgJson = new String(message.getBody());
                System.out.println("Returned Message：" + msgJson);
            }
        };
    }

}
