package com.gmall.springdemo.CircularDependencies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-20 21:14:01
 * @description
 */
@Component
public class CircularDependencies {

    @Autowired
    private CircularDependencies circularDependencies;
}
