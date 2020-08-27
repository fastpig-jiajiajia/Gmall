package com.gmall.jbasedemo.aop;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


/**
 * 通过一个方法上的多个注解，
 * 代理的执行顺序和注解的顺序无关，和代理方法的顺序也无关；
 * 和字母排序相关
 * 可以使用 @Order 来配置执行顺序，值越小，优先级越高。
 */
@Aspect
@Component
public class AspectClass1 {

    @Pointcut("@annotation(com.gmall.jbasedemo.aop.ResourceCheck)")
    private void pointCut() {

    }

    @Pointcut("@annotation(com.gmall.jbasedemo.aop.AccessCheck)")
    private void pointCut1() {

    }

    @Pointcut("@annotation(com.gmall.jbasedemo.aop.Cuzz)")
    private void pointCut2() {

    }

    @Before("pointCut()")
    public void doResourceCheckBefore(){
        System.out.println("resourceCheckBefore1");
    }

    @Before("pointCut1()")
    public void doAccessCheckBefore(){
        System.out.println("accessCheckBefore1");
    }

    @Before("pointCut2()")
    public void doCuzzBefore(){
        System.out.println("cuzzBefore1");
    }

    @After("pointCut()")
    public void doResourceCheckAfter(){
        System.out.println("resourceCheckAfter1");
    }

}
