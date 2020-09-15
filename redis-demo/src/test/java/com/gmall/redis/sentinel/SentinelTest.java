package com.gmall.redis.sentinel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/*
 * AUTHOR james
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SentinelTest {

    @Resource
    private SentinelTemplateUtil service;

    @Test
    public void testSet() throws InterruptedException {
        service.set("name12", "jamesssss");
        String str = service.get("name12");

        System.out.println("=====" + str);
    }

    @Test
    public void testGet() throws InterruptedException {

        System.out.println("=============" + service.get("age1"));
    }
}
