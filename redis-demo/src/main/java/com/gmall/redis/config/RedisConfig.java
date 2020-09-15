package com.gmall.redis.config;

import com.gmall.redis.configuration.RedisPropertiesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class RedisConfig {

    @Autowired
    RedisPropertiesConfig redisPropertiesConfig;

    private Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();


//    @Bean
//    public JedisPoolConfig jedisPoolConfig() {
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(10);
//        jedisPoolConfig.setMaxTotal(10000);
//        return jedisPoolConfig;
//    }
//
//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig)  {
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
//        jedisConnectionFactory.setHostName("39.101.198.56");
//        jedisConnectionFactory.setPort(6379);
//        jedisConnectionFactory.setPassword("Xr20190101!");
//        jedisConnectionFactory.setUsePool(true);
//        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
//
//        return jedisConnectionFactory;
//    }


    /**
     * 初始化 redis 集群
     */
    @Bean
    @Primary
    public JedisCluster initPool() {
        // 配置节点
        jedisClusterNodes.add(new HostAndPort("39.101.198.56", 6379));
        jedisClusterNodes.add(new HostAndPort("39.101.198.56", 6380));
        jedisClusterNodes.add(new HostAndPort("39.101.198.56", 6381));
        jedisClusterNodes.add(new HostAndPort("39.101.198.56", 6389));
        jedisClusterNodes.add(new HostAndPort("39.101.198.56", 6390));
        jedisClusterNodes.add(new HostAndPort("39.101.198.56", 6391));
        // 配置池化
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(2000);
        poolConfig.setMaxIdle(50);
        poolConfig.setMinIdle(8);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(10 * 1000);
        poolConfig.setTestOnBorrow(redisPropertiesConfig.getTestOnBorrow());
        poolConfig.setTestOnReturn(false);
        //Idle时进行连接扫描
        poolConfig.setTestWhileIdle(true);
        //表示idle object evitor两次扫描之间要sleep的毫秒数
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        //表示idle object evitor每次扫描的最多的对象数
        poolConfig.setNumTestsPerEvictionRun(10);
        //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        poolConfig.setMinEvictableIdleTimeMillis(60000);

        JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes, 20 * 1000, poolConfig);
        return jedisCluster;
    }
}
