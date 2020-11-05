package com.gmall.springdemo.abstractclass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public abstract class ParentAbstractClass {

    @Autowired
    private Demo demo;

    public String printParent(String path) {
        return "";
    }

    public String parentA() {
        return demo.toString();
    }
}
