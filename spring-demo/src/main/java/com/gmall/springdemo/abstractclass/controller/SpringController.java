package com.gmall.springdemo.abstractclass.controller;

import com.gmall.springdemo.abstractclass.ParentAbstractClass;
import com.gmall.springdemo.abstractclass.SonClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class SpringController {

    @Autowired
    private SonClass sonClass;

    @Resource(name = "mapBean")
    private Map<String, ParentAbstractClass> map;

    @RequestMapping(value = "/print/{name}")
    public String print(@PathVariable("name") String name){
        System.out.println("map.get(\"2\")  " + map.get("2").printParent(name));
        System.out.println("map.get(\"3\")  " + map.get("3").parentA());
        System.out.println("sonClass.parentA()" + sonClass.parentA());
//        System.out.println(map.get("4").printParent(name));
        return map.get("1").printParent(name);
    }

}
