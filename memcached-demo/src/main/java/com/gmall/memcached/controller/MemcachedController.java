package com.gmall.memcached.controller;

import com.gmall.memcached.config.MemcachedRunner;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/memcached")
public class MemcachedController {


    @Autowired
    private MemcachedClient client;


    @RequestMapping("/getInfo/id")
    public String getInfo(@PathVariable String id){
        System.out.println(client.set("111", 1000,"222"));

        System.out.println(client.get("111"));


        return null;
    }
}
