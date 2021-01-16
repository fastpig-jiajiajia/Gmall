package com.gmall.springdemo.importAnnotation.config;

import com.gmall.springdemo.importAnnotation.ImportDemo1;
import com.gmall.springdemo.importAnnotation.ImportDemo3;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-13 23:00:30
 * @description
 */
@Component
@Import(value = {ImportConfiguration3.class})
public class ImportConfiguration3 implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        //指定bean定义信息（包括bean的类型、作用域...）
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(ImportDemo3.class);
        //注册一个bean指定bean名字（id）
        beanDefinitionRegistry.registerBeanDefinition("importDemo3", rootBeanDefinition);
    }
}
