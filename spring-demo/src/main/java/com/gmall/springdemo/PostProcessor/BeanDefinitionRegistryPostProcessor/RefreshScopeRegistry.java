package com.gmall.springdemo.PostProcessor.BeanDefinitionRegistryPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class RefreshScopeRegistry implements BeanDefinitionRegistryPostProcessor {

    private BeanDefinitionRegistry beanDefinitionRegistry;


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.beanDefinitionRegistry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 注册自定义的作用域
//        beanFactory.registerScope(RefreshConstant.SCOPE_NAME, new RefreshScope());
        System.out.println("RefreshScopeRegistry postProcessBeanFactory");
        // 移除不需要的BeanDefinition
        BeanDefinitionRegistry beanFactory1 = (BeanDefinitionRegistry) beanFactory;
//        beanFactory1.removeBeanDefinition("sonClass");
    }
}