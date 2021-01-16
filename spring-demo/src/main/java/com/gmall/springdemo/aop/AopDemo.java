package com.gmall.springdemo.aop;

import com.gmall.springdemo.aop.aaa.Cuzz;
import org.springframework.stereotype.Component;

@Component
public class AopDemo {

    @ResourceCheck(code = "test_check")
    @Cuzz
    @AccessCheck(code = "test_check")
    public void test(){
        System.out.println("test demo");
        return ;
    }
}
