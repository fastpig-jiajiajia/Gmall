package com.gmall.refresh.scope;

import com.gmall.refresh.constant.RefreshConstant;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 自定义作用域，并注册进入 Spring容器
 */
@Data
@Component
public class RefreshScopeRegistry implements BeanDefinitionRegistryPostProcessor {

    private BeanDefinitionRegistry beanDefinitionRegistry;


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.beanDefinitionRegistry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerScope(RefreshConstant.SCOPE_NAME, new RefreshScope());
    }
}
