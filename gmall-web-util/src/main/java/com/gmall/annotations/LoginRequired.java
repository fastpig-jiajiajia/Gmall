package com.gmall.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
    /**
     * 是否登录，用以确定是否需要登录才可以进行后续的操作
     * 默认 true 必须登陆后才操作
     * @return
     */
    boolean loginSuccess() default true;

}
