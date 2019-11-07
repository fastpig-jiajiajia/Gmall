package com.gmall.item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class ItemController {

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("nice")
    public String nice(){
        System.out.println("what");
        return "nice";
    }

    public void feature1(){

    }

}
