package com.gmall.springdemo.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testController")
public class TestController {

    @Autowired
    private AopDemo aopDemo;

    @RequestMapping("/test")
    public void test(){
        aopDemo.test();
    }
}
