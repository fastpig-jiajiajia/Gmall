package com.gmall.jbasedemo.aop;

import org.springframework.stereotype.Component;

@Component
public class AopDemo {


    @AccessCheck(code = "test_check")
    @ResourceCheck(code = "test_check")
    @Cuzz
    public void test(){
        System.out.println("test demo");
    }
}
