package com.gmall.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitConfig {

    @Value("${spring.rabbitmq.queueName:disabled}")
    private String queueName;
    @Value("${spring.rabbitmq.durable:true}")
    private Boolean durable;
    @Value("${spring.rabbitmq.exchangeName:amq.direct}")
    private String exchangeName;
    @Value("${spring.rabbitmq.routeKey:#}")
    private String routeKey;


    //队列
    @Bean
    public Queue topicQueue() {
        return new Queue(queueName, durable);  //true 是否持久
    }

    // TopicExchange: 主题路由
    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(exchangeName);
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
    @Bean
    Binding topicBinding(){
        return BindingBuilder.bind(topicQueue()).to(topicExchange()).with(routeKey);
    }
}
