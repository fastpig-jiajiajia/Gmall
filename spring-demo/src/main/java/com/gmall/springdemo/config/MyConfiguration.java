package com.gmall.springdemo.config;

import com.gmall.springdemo.beanpostprocessor.MyBeanPostProcessor;
import org.springframework.context.annotation.Bean;

/**
 * @author rui.xu
 * @date 2020/10/09 14:11
 * @description
 **/
@org.springframework.context.annotation.Configuration
public class MyConfiguration {

    @Bean
    MyBeanPostProcessor getMyBeanPostProcessor(){
        return new MyBeanPostProcessor();
    }
}
