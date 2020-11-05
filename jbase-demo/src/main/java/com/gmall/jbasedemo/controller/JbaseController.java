package com.gmall.jbasedemo.controller;

import com.gmall.jbasedemo.autowire.AutowireMapAndListByType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rui.xu
 * @date 2020/09/22 15:55
 * @description
 **/
@RestController
@RequestMapping("/jbasecontroller")
public class JbaseController {

    @Autowired
    private AutowireMapAndListByType autowireMapAndListByType;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){
        autowireMapAndListByType.test();
        return "SUCCESS";
    }
}
