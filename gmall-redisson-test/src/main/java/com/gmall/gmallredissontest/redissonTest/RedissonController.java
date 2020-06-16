package com.gmall.gmallredissontest.redissonTest;

import com.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.ShardedJedis;

import java.util.concurrent.TimeUnit;

@Scope("request")
@RestController
public class RedissonController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 单机应用时锁的使用,
     * 上锁时设置了过期时间，防止死锁，
     * 设置的锁时 hash 类型，可重入锁
     * field 是客户端的 hash id，
     * value 是被重复上锁的次数，每被上一次锁，就会被加1，释放锁时需要将该参数逐渐减一到 0
     * @return
     */
    @RequestMapping("/testRedisson")
    public String testRedisson(){
        ShardedJedis shardedJedis = redisUtil.getShardedJedis();
        RLock lock = redissonClient.getLock("lock");// 声明锁
        lock.lock(100, TimeUnit.SECONDS);//上锁，设置过期时间，防止死锁的产生

        boolean isLock = false;
        try {
            // 第二次上锁，设置尝加锁时间 100s，加锁成功有效时间 10s，如果只解锁一次时，hash 字段对应的 值只是1，需要释放两次
            isLock = lock.tryLock(100, 100, TimeUnit.SECONDS);
            if(isLock){
                shardedJedis.ttl("lock");
            }
            String v = shardedJedis.get("k");
            if (StringUtils.isBlank(v)) {
                v = "1";
            }
            System.out.println("->" + v);
            shardedJedis.set("k", (Integer.parseInt(v) + 1) + "");
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            shardedJedis.close();
            lock.unlock();// 解锁，因为上面加锁了两次，所以这里需要解锁两次
            lock.unlock();
        }
        return "success";
    }


    /**
     * redLock 分布式锁的使用
     */
    @RequestMapping("/getRedLock/id")
    public String geRredLock(@PathVariable String id){
        // 分片 redis
        ShardedJedis shardedJedis = redisUtil.getShardedJedis();

        // 设置参与分布式锁的主机
        Config config1 = new Config();
        config1.useSingleServer().setAddress("redis://172.29.1.180:5378")
                .setPassword("a123456").setDatabase(0);
        RedissonClient redissonClient1 = Redisson.create(config1);

        Config config2 = new Config();
        config2.useSingleServer().setAddress("redis://172.29.1.180:5379")
                .setPassword("a123456").setDatabase(0);
        RedissonClient redissonClient2 = Redisson.create(config2);

        Config config3 = new Config();
        config3.useSingleServer().setAddress("redis://172.29.1.180:5380")
                .setPassword("a123456").setDatabase(0);
        RedissonClient redissonClient3 = Redisson.create(config3);

        // 为每个集群上锁
        String rLock = "rLock";
        RLock lock1 = redissonClient1.getLock(rLock);
        RLock lock2 = redissonClient2.getLock(rLock);
        RLock lock3 = redissonClient3.getLock(rLock);

        // 获取 redlock
        RedissonRedLock redlock = new RedissonRedLock(lock1, lock2, lock3);
        boolean isLock = false;
        try{
            isLock = redlock.tryLock(100, 10, TimeUnit.SECONDS);

            if(isLock){
                TimeUnit.SECONDS.sleep(8);
                System.out.println(shardedJedis.ttl(rLock));
            }else{
                System.out.println("redlock failed");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            shardedJedis.close();
            lock1.unlock();
            lock2.unlock();
            lock3.unlock();
            redlock.unlock();
        }


        return id;
    }



}
