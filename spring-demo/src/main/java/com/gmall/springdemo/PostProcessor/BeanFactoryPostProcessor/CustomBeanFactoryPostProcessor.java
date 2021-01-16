package com.gmall.springdemo.PostProcessor.BeanFactoryPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    /**
     * 主要是用来自定义修改持有的bean
     * ConfigurableListableBeanFactory 其实就是DefaultListableBeanDefinition对象
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("调用了自定义的BeanFactoryPostProcessor " + beanFactory);
        Iterator it = beanFactory.getBeanNamesIterator();

//        String[] names = beanFactory.getBeanDefinitionNames();
//        // 获取了所有的bean名称列表
//        for (int i = 0; i < names.length; i++) {
//            String name = names[i];
//
//            BeanDefinition bd = beanFactory.getBeanDefinition(name);
//            System.out.println(name + " bean properties: " + bd.getPropertyValues().toString());
//            // 本内容只是个demo，打印持有的bean的属性情况
//        }
        // 获取到 BeanDefinition 在为实例化之前进行修改，例如修改作用域等
        beanFactory.destroyBean("sonClass");
        // 注册自定义作用域
//        beanFactory.registerScope();

        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("myConfiguration");
        BeanDefinition beanDefinition1 = beanFactory.getBeanDefinition("getMyBeanPostProcessor");
        System.out.println();
    }
}