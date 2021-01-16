package com.gmall.jbasedemo.reflect.cache.aop;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.gmall.jbasedemo.reflect.cache.annotation.ResourceCheck;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-28 21:49:26
 * @description
 */
@Aspect
@Component
public class ResourceAction {


    @Before("@annotation(com.gmall.jbasedemo.reflect.cache.annotation.ResourceCheck)")
    public void handleControllerMethod(JoinPoint joinPoint, ResourceCheck resourceCheck) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        System.out.println(joinPoint.getTarget().getClass().getName());
        System.out.println(methodSignature.getMethod().getName());
        System.out.println(joinPoint.getSignature().getName());
        System.out.println(joinPoint.getArgs());

        ConstructorAccess constructorAccess = ConstructorAccess.get(joinPoint.getArgs()[0].getClass());
        MethodAccess methodAccess = MethodAccess.get(joinPoint.getArgs()[0].getClass());
        FieldAccess fieldAccess = FieldAccess.get(joinPoint.getArgs()[0].getClass());
    }

}
