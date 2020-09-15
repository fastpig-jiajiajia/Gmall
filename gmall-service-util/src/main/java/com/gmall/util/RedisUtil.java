package com.gmall.util;

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

    public void initPool(String host, int port, int database, String password, boolean testOnBorrow) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(2000);
        poolConfig.setMaxIdle(50);
        poolConfig.setMinIdle(8);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(10 * 1000);
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
        jedisPool = new JedisPool(poolConfig, host, port, 20 * 1000, password);

        JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port);
        jedisShardInfo.setPassword(password);
        List<JedisShardInfo> list = new LinkedList<JedisShardInfo>();
        list.add(jedisShardInfo);
        shardedJedisPool = new ShardedJedisPool(poolConfig, list);
    }

    /**
     * 获取 ShardedJedis
     *
     * @return
     */
    public ShardedJedis getShardedJedis() {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        return shardedJedis;
    }

    /**
     * 获取 Jedis
     *
     * @return
     */
    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

}


//package com.gmall.util;
//
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//import redis.clients.jedis.*;
//
//import java.awt.image.VolatileImage;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//@Component
//public class RedisUtil {
//    /**
//     * Jedis
//     */
//    private static volatile JedisPool jedisPool;
//    private static volatile Jedis jedis;
//    private static volatile ShardedJedis shardedJedis;
//    /**
//     * ShardedJedis 实现一致性哈希的 Jedis
//     */
//    private static ShardedJedisPool shardedJedisPool;
//
//    private static Lock reentrantLock = new ReentrantLock();
//
//    public static void initPool(String host,int port ,int database, String password, boolean testOnBorrow){
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(2000);
//        poolConfig.setMaxIdle(50);
//        poolConfig.setMinIdle(8);
//        poolConfig.setBlockWhenExhausted(true);
//        poolConfig.setMaxWaitMillis(10*1000);
//        poolConfig.setTestOnBorrow(testOnBorrow);
//        poolConfig.setTestOnReturn(false);
//        //Idle时进行连接扫描
//        poolConfig.setTestWhileIdle(true);
//        //表示idle object evitor两次扫描之间要sleep的毫秒数
//        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
//        //表示idle object evitor每次扫描的最多的对象数
//        poolConfig.setNumTestsPerEvictionRun(10);
//        //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
//        poolConfig.setMinEvictableIdleTimeMillis(60000);
//
//        if(jedisPool == null){
//            reentrantLock.lock();
//            if(jedisPool == null){
//                jedisPool = new JedisPool(poolConfig, host, port,20*1000, password);
//            }
//            reentrantLock.unlock();
//        }
//
//        if(shardedJedisPool == null){
//            reentrantLock.lock();
//            if(shardedJedisPool == null){
//                JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port);
//                jedisShardInfo.setPassword(password);
//                List<JedisShardInfo> list = new LinkedList<JedisShardInfo>();
//                list.add(jedisShardInfo);
//                shardedJedisPool = new ShardedJedisPool(poolConfig, list);
//            }
//            reentrantLock.unlock();
//        }
//
//    }
//
//    /**
//     * 获取 ShardedJedis
//     * @return
//     */
//    public ShardedJedis getShardedJedis(){
//        if(shardedJedis == null){
//            reentrantLock.lock();
//            if(shardedJedis == null){
//                shardedJedis = shardedJedisPool.getResource();
//            }
//            reentrantLock.unlock();
//        }
//        return shardedJedis;
//    }
//
//    /**
//     * 获取 Jedis
//     * memory=allocate();//1.分配对象内存空间
//     instance(memory);//2.初始化对象
//     instance=memory;//3.设置instance的指向刚分配的内存地址,此时instance!=null
//     不取消指令重排，会出现以下情况：在 instance=new SingletonDemo() 时，指令重排，执行顺序改为 132，
//     此时 instance 指针先指向被分配的内存，但是该内存还未被初始化赋值，导致等待的线程判空是出现误判，不为空，直接返回一个没被初始化赋值的对象，
//     但是此时该指针指向的内存为空，导致调用时空指针。
//     * @return
//     */
//    public Jedis getJedis(){
//        if (jedis == null){
//            reentrantLock.lock();
//            if(jedis == null){
//                jedis = jedisPool.getResource();
//            }
//            reentrantLock.unlock();
//        }
//        return jedis;
//    }
//}
//


