package com.gmall.memcached.config;

import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 初始化 memcached 连接客户端
 * 继承 CommandLineRunner 实现初始化操作。
 */
@Configuration
public class MemcachedRunner implements CommandLineRunner {
    protected Logger logger =  LoggerFactory.getLogger(this.getClass());

    @Resource
    private  MemcachedConfig memcachedConfig;

    private MemcachedClient client = null;

    @Override
    public void run(String... args) throws Exception {
        try {
            client = new MemcachedClient(new InetSocketAddress(memcachedConfig.getIp(), memcachedConfig.getPort()));
        } catch (IOException e) {
            logger.error("inint MemcachedClient failed ",e);
        }
    }

    @Bean
    public MemcachedClient getMemcachedClient() {
        try {
            client = new MemcachedClient(new InetSocketAddress(memcachedConfig.getIp(), memcachedConfig.getPort()));
        } catch (IOException e) {
            logger.error("inint MemcachedClient failed ",e);
        }
        return client;
    }

}
