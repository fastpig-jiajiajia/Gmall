package com.gmall.redis.sentinel;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.james.cache.service.SentinelTemplateUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

/*
 * AUTHOR james
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SentinelTest {
	
	@Resource
	private SentinelTemplateUtil service;
	
	@Test
	public void testSet() throws InterruptedException{
		 service.set("name12", "jamesssss");
	     String str = service.get("name12");   
		 
    	 System.out.println("====="	+str);
	}	
	
	@Test
	public void testGet() throws InterruptedException{
		 
		System.out.println("============="+service.get("age1"));
	}
}
