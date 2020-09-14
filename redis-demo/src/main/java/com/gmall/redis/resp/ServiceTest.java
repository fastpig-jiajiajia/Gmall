package com.gmall.redis.resp;

import com.james.cache.jedis.RedisTools;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class ServiceTest {

    public static void main(String[] args) {
        RedisTools.initRedisData();
        long t = System.currentTimeMillis();
        delNoPipe(RedisTools.keys);
        System.out.println(System.currentTimeMillis()-t);
    }

    public static void delNoStus(String...keys){
        Jedis jedis = new Jedis(RedisTools.ip,RedisTools.port);
        for(String key:keys){
            jedis.del(key);
        }
        jedis.close();
    }
    public static void delNoPipe(String...keys){
        Jedis jedis = new Jedis(RedisTools.ip,RedisTools.port);
        Pipeline pipelined = jedis.pipelined();
        for(String key:keys){
            pipelined.del(key);//redis?
        }
        pipelined.sync();//
        jedis.close();
    }
}
