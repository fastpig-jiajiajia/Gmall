package com.gmall.jbasedemo.autowire;

import org.springframework.stereotype.Component;

@Component("serviceClass1")
public class ServiceClass1 implements BaseInterface {

    public void print() {
        System.out.println("service-1");
    }
}
