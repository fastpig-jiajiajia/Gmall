package com.gmall.gmallredissontest.redissonTest;

import com.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.util.concurrent.TimeUnit;

@Controller
public class RedissonController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("testRedisson")
    @ResponseBody
    public String testRedisson(){
        ShardedJedis shardedJedis = redisUtil.getShardedJedis();
        RLock lock = redissonClient.getLock("lock");// 声明锁
        lock.lock(10, TimeUnit.SECONDS);//上锁，设置过期时间，防止死锁的产生
        try {
            String v = shardedJedis.get("k");
            if (StringUtils.isBlank(v)) {
                v = "1";
            }
            System.out.println("->" + v);
            shardedJedis.set("k", (Integer.parseInt(v) + 1) + "");
        }finally {
            shardedJedis.close();
            lock.unlock();// 解锁
        }
        return "success";
    }
}
