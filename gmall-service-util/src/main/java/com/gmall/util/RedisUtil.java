package com.gmall.util;

import org.springframework.context.annotation.ComponentScan;
import redis.clients.jedis.*;

import java.util.LinkedList;
import java.util.List;

public class RedisUtil {
    /**
     * Jedis
     */
    private static JedisPool jedisPool;
    /**
     * ShardedJedis 实现一致性哈希的 Jedis
     */
    private static ShardedJedisPool shardedJedisPool;

    public void initPool(String host,int port ,int database, String password, boolean testOnBorrow){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(2000);
        poolConfig.setMaxIdle(50);
        poolConfig.setMinIdle(8);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(10*1000);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(false);
        //Idle时进行连接扫描
        poolConfig.setTestWhileIdle(true);
        //表示idle object evitor两次扫描之间要sleep的毫秒数
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        //表示idle object evitor每次扫描的最多的对象数
        poolConfig.setNumTestsPerEvictionRun(10);
        //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        jedisPool = new JedisPool(poolConfig, host, port,20*1000, password);

        JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port);
        jedisShardInfo.setPassword(password);
        List<JedisShardInfo> list = new LinkedList<JedisShardInfo>();
        list.add(jedisShardInfo);
        shardedJedisPool = new ShardedJedisPool(poolConfig, list);
    }

    /**
     * 获取 ShardedJedis
     * @return
     */
    public ShardedJedis getShardedJedis(){
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        return shardedJedis;
    }

    /**
     * 获取 Jedis
     * @return
     */
    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

}

