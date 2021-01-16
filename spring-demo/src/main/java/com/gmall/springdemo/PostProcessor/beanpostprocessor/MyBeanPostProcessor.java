package com.gmall.springdemo.PostProcessor.beanpostprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

/**
 * @author rui.xu
 * @date 2020/10/09 14:07
 * @description
 **/
public class MyBeanPostProcessor implements BeanPostProcessor {


    /**
     * Bean 实例化完成之后，初始化之前调用
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor BeforeInitialization  --- " + beanName);
        return bean;
    }

    /**
     * Bean 初始化完成后调用
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor AfterInitialization --- " + beanName);
        return bean;
    }
}
