package com.gmall.springdemo.abstractclass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AutowireClass {

    @Autowired
    private SonClass sonClass;

    @Resource(name = "sonClass")
    private ParentAbstractClass parentClass;

    @Resource(name = "son2Class")
    private ParentAbstractClass parentClass1;

//    @Resource(name = "parentAbstractClass")
//    private ParentAbstractClass parentClass2;

    @Bean
    public Map<String, ParentAbstractClass> mapBean() {
        Map<String, ParentAbstractClass> map = new HashMap<>();
        map.put("1", sonClass);
        map.put("2", parentClass);
        map.put("3", parentClass1);
//        map.put("4", parentClass2);
        return map;
    }

}
