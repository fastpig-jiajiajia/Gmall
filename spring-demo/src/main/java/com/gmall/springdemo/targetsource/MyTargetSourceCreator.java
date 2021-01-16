package com.gmall.springdemo.targetsource;

import org.springframework.aop.framework.autoproxy.target.AbstractBeanFactoryBasedTargetSourceCreator;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class MyTargetSourceCreator extends AbstractBeanFactoryBasedTargetSourceCreator {
    @Override
    protected AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(Class<?> beanClass, String beanName) {

        if (getBeanFactory() instanceof ConfigurableListableBeanFactory) {
            BeanDefinition definition =
                    ((ConfigurableListableBeanFactory) getBeanFactory()).getBeanDefinition(beanName);

            if(beanName.equalsIgnoreCase("user1222")) {
                return new MyTargetSource();
            }
        }

        return null;
    }
}