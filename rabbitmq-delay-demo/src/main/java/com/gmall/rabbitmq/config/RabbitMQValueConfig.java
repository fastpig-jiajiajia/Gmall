package com.gmall.rabbitmq.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author rui.xu
 * @date 2020/11/05 16:50
 * @description
 **/
@Data
@Component
public class RabbitMQValueConfig {

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



}
