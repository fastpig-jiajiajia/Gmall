package com.gmall.springdemo.PostProcessor.InstantiationAwareBeanPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-14 21:08:39
 * @description
 */
@Component
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

//    @Override
//    @Nullable
//    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
//        System.out.println("MyInstantiationAwareBeanPostProcessor Before: " + beanName);
//        try {
//            return beanClass.newInstance();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /*
     * 可以使所有类不能ioc依赖注入
     * */
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        System.out.println("MyInstantiationAwareBeanPostProcessor After: " + beanName);
        return true;
    }

    @Override
    @Nullable
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        System.out.println("MyInstantiationAwareBeanPostProcessor postProcessProperties: " + beanName);
        return pvs;
    }

    @Deprecated
    @Nullable
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return pvs;
    }
}
