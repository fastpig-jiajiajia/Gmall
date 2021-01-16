package com.gmall.springdemo.configuration;

import com.gmall.springdemo.PostProcessor.beanpostprocessor.MyBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author rui.xu
 * @date 2020/10/09 14:11
 * @description
 **/
@Configuration
public class MyConfiguration {

    @Bean
    MyBeanPostProcessor getMyBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }
}
