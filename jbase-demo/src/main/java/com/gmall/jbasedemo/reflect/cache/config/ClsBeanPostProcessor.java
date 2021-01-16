package com.gmall.jbasedemo.reflect.cache.config;

import com.gmall.jbasedemo.reflect.cache.ClsCacheUtil;
import com.gmall.jbasedemo.reflect.cache.annotation.ResourceCheck;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-27 22:56:17
 * @description
 */
@Component
public class ClsBeanPostProcessor implements BeanPostProcessor {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getMethods();
        Arrays.stream(methods).forEach(method -> {
            ResourceCheck resourceCheck1 = AnnotationUtils.findAnnotation(method, ResourceCheck.class);
            method.getDeclaredAnnotations();
            ResourceCheck resourceCheck = method.getDeclaredAnnotation(ResourceCheck.class);
            if (resourceCheck != null) {
                String[] resourceIds = resourceCheck.resourceIds();
                Parameter[] parameters = method.getParameters();
                Arrays.stream(parameters).forEach(parameter -> {
                    Field[] fields = parameter.getClass().getDeclaredFields();
                    Map<String, Class> map = new HashMap<>();
                    Arrays.stream(fields).forEach(field -> {
                        for (String resourceId : resourceIds) {
                            if (resourceId.equals(field.getName())) {
                                map.put(field.getName(), field.getType());
                            }
                        }
                    });
                    ClsCacheUtil.map.put(bean.getClass().getName() + method.getName() + parameter.getType().getClass().getName(), map);
                });

            }
        });

        return bean;
    }
}
