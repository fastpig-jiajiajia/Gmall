package com.gmall.util;

import redis.clients.jedis.*;

import java.util.LinkedList;
import java.util.List;

public class RedisUtil {

    private JedisPool jedisPool;
    private static ShardedJedisPool pool;

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
        pool = new ShardedJedisPool(poolConfig, list);
    }

    public ShardedJedis getJedis(){
        ShardedJedis shardedJedis = pool.getResource();
        return shardedJedis;
    }

}

