package com.gmall.redis.sentinel;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

public class JedisSentinel {

    @Test
    public void testJedis() throws InterruptedException {
        Set<String> sentinels = new HashSet<String>();
        String hostAndPort1 = "192.168.42.111:26379";
        String hostAndPort2 = "192.168.42.111:26380";
        String hostAndPort3 = "192.168.42.111:26381";
        sentinels.add(hostAndPort1);
        sentinels.add(hostAndPort2);
        sentinels.add(hostAndPort3);

        String clusterName = "mymaster";
        String password = "12345678";

        JedisSentinelPool redisSentinelJedisPool = new JedisSentinelPool(clusterName, sentinels, password);

        Jedis jedis = null;
        try {
            jedis = redisSentinelJedisPool.getResource();

            jedis.set("name", "james1111111");
            System.out.println(jedis.get("name"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisSentinelJedisPool.returnBrokenResource(jedis);
        }

        redisSentinelJedisPool.close();
    }

}
