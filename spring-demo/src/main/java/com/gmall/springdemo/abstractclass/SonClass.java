package com.gmall.springdemo.abstractclass;

import org.springframework.stereotype.Component;

@Component
public class SonClass extends ParentAbstractClass {

    @Override
    public String printParent(String path){
        return "printSon: " + path;
    }

}
