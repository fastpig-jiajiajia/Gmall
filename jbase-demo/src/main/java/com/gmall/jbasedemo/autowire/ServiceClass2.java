package com.gmall.jbasedemo.autowire;

import org.springframework.stereotype.Component;

@Component("serviceClass2")
public class ServiceClass2 implements BaseInterface {

    public void print() {
        System.out.println("service-2");
    }
}
