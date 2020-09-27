package com.gmall.springdemo.abstractclass;

import org.springframework.stereotype.Component;

@Component
public class Son2Class extends ParentAbstractClass {

    @Override
    public String printParent(String path) {
        return "Son2Class: " + path;
    }

}
