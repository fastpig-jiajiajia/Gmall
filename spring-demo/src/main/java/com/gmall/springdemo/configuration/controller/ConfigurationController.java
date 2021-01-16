package com.gmall.springdemo.configuration.controller;

import com.gmall.springdemo.PostProcessor.beanpostprocessor.MyBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-13 22:39:45
 * @description
 */
@RestController
public class ConfigurationController {

    @Resource(name = "getMyBeanPostProcessor")
    private MyBeanPostProcessor myBeanPostProcessor;

    @GetMapping("/configurationController")
    public String getConfiguration(){
        if(true){
            throw new RuntimeException("11");
        }
        return myBeanPostProcessor.getClass().getName();
    }

}
