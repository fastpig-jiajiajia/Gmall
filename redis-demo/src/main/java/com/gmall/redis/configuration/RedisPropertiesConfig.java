package com.gmall.redis.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author rui.xu
 * @date 2020/09/15 16:25
 * @description
 **/
@Data
@Configuration // spring 配置类
public class RedisPropertiesConfig {
    //读取配置文件中的redis的ip地址，配置文件放在调用者的工程下，不同的工程可以配置不同的redis连接
    @Value("${spring.redis.host:39.101.198.56}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.testOnBorrow:false}")
    private Boolean testOnBorrow;

    @Value("${spring.redis.testOnReturnt:false}")
    private Boolean testOnReturnt;

    @Value("${spring.redis.pool.max-active:20}")
    private Integer maxActive;

    @Value("${spring.redis.pool.max-wait:5}")
    private Integer maxWait;

    @Value("${spring.redis.pool.min-idle:5}")
    private Integer minIdle;

    @Value("${spring.redis.timeout:20000}")
    private Integer timeout;

}
