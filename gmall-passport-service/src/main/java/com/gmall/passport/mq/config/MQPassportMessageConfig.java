//package com.gmall.passport.mq.config;
//
//import com.rabbitmq.client.impl.AMQImpl;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.core.Queue;
//import org.springframework.context.annotation.Primary;
//
//
//@Configuration
//public class MQPassportMessageConfig {
//
//    private final static String mergeCartExchange = "LOGIN_MERGECART_EXCHANGE";
//    private final static String mergeCartQueue = "LOGIN_MERGECART_QUEUE";
//
//    /**
//     * 定义一个hello的队列
//     * Queue 可以有4个参数
//     *      1.队列名
//     *      2.durable       持久化消息队列 ,rabbitmq重启的时候不需要创建新的队列 默认true
//     *      3.auto-delete   表示消息队列没有在使用时将被自动删除 默认是false
//     *      4.exclusive     表示该消息队列是否只在当前connection生效,默认是false
//     */
//    @Bean
//    public Queue mergeCartQueue() {
//        return new Queue(mergeCartQueue);
//    }
//
//    @Bean
//    TopicExchange mergeCartExchange(){
//        return new TopicExchange(mergeCartExchange);
//    }
//
//    @Bean
//    @Primary
//    Binding bindingExchangeQueue(Queue queue, TopicExchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with("gmall.#");
//    }
//
//
//
//
//}
