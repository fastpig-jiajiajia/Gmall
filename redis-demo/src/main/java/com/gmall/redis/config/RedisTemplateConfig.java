package com.gmall.redis.config;

/**
 * @author rui.xu
 * @date 2020/09/16 14:21
 * @description
 **/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisTemplateConfig {

    @Bean
    @Primary
    RedisClusterConfiguration redisClusterConfguration() {
        RedisClusterConfiguration configuration = new RedisClusterConfiguration();
        List<RedisNode> nodes = new ArrayList<>();
        nodes.add(new RedisNode("39.101.198.56", 6379));
        nodes.add(new RedisNode("39.101.198.56", 6380));
        nodes.add(new RedisNode("39.101.198.56", 6381));
        nodes.add(new RedisNode("39.101.198.56", 6389));
        nodes.add(new RedisNode("39.101.198.56", 6390));
        nodes.add(new RedisNode("39.101.198.56", 6391));
        configuration.setClusterNodes(nodes);
        return configuration;
    }

}
