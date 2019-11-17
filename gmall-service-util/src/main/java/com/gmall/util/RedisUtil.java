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

    public void initPool(String host,int port ,int database, String password){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxIdle(30);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(10*1000);
        poolConfig.setTestOnBorrow(true);
        jedisPool = new JedisPool(poolConfig,host,port,20*1000);

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

