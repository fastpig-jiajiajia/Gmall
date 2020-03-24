package com.gmall.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("testController")
public class TetsController {
    Integer i = 0;

    @RequestMapping("modifyNum")
    public int modifyNum(){
        return ++i;

    }

}
