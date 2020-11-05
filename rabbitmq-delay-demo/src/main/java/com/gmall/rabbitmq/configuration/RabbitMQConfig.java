package com.gmall.rabbitmq.configuration;

import com.gmall.rabbitmq.config.RabbitMQValueConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author rui.xu
 * @date 2020/11/05 16:58
 * @description
 **/
@Configuration
public class RabbitMQConfig {

    @Autowired
    private RabbitMQValueConfig config;


    @Bean
    public ConnectionFactory connectionFactory() {
        String userName = config.getUserName();
        String password  = config.getPassword();
        Integer port = config.getPort();
        String host = config.getHost();
        String virtualHost = config.getVirtualHost();
        Boolean publisherConfirms = config.getPublisherConfirms();
        Boolean publisherReturns = config.getPublisherReturns();

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(publisherConfirms);
        connectionFactory.setPublisherReturns(publisherReturns);
        return connectionFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }

}
