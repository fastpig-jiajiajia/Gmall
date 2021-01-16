package com.gmall.springdemo.aop;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


/**
 * 通过一个方法上的多个注解，
 * 代理的执行顺序和注解的顺序无关，和代理方法的顺序也无关；
 * 和字母排序相关
 * 可以使用 @Order 来配置执行顺序，
 * @Before 值越小，优先级越高。
 * @After 值越小，优先级越低
 *
 */
@Aspect
@Component
public class AspectClass2 {

    @Pointcut("@annotation(com.gmall.springdemo.aop.ResourceCheck)")
    private void pointCut() {

    }

    @Pointcut("@annotation(com.gmall.springdemo.aop.AccessCheck)")
    private void pointCut1() {

    }

    @Pointcut("@annotation(com.gmall.springdemo.aop.aaa.Cuzz)")
    private void pointCut2() {

    }

    @Before("pointCut()")
    public void doResourceCheckBefore(){
        System.out.println("resourceCheckBefore");
    }

    @Before("pointCut1()")
    public void doAccessCheckBefore(){
        System.out.println("accessCheckBefore");
    }

    @Before("pointCut2()")
    public void doCuzzBefore(){
        System.out.println("cuzzBefore");
    }

    @After("pointCut()")
    public void doResourceCheckAfter(){
        System.out.println("resourceCheckAfter");
    }

}
