package com.gmall.springdemo.abstractclass;

import org.springframework.stereotype.Component;

@Component
public abstract class ParentAbstractClass {


    public String printParent(String path){
        return "printParent: " + path;
    }
}
