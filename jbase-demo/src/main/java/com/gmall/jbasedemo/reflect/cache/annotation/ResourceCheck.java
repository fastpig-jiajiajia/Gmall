package com.gmall.jbasedemo.reflect.cache.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020/12/27 22:52
 * @description
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
@Inherited  // 必须配置这个
public @interface ResourceCheck {

    String[] resourceIds();
}
