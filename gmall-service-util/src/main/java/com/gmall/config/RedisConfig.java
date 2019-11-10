package com.gmall.config;

import com.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // spring 配置类
public class RedisConfig {
    //读取配置文件中的redis的ip地址，配置文件放在调用者的工程下，不同的工程可以配置不同的redis连接
    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port ;

    @Value("${spring.redis.database:0}")
    private int database;

    @Value("${spring.redis.password}")
    private String password;

    // spring 容器加载时就将其加载进来
    @Bean
    public RedisUtil getRedisUtil(){
        if(host.equals("disabled")){
            return null;
        }
        RedisUtil redisUtil=new RedisUtil();
        // 初始化连接池
        redisUtil.initPool(host, port, database, password);
        return redisUtil;
    }
}
