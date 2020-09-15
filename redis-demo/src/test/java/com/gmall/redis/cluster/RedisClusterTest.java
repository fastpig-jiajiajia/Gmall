package com.gmall.redis.cluster;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;

/*
 * redis 集群配置
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisClusterTest {

    @Autowired
    private JedisCluster jedisCluster;

    @Test
    public void testPutCache() throws Exception {

        jedisCluster.set("class", "112233");

        System.out.println(jedisCluster.get("class"));
    }

    @Test
    public void testBasic() throws IOException {
        jedisCluster.set("james:age", "18");

        System.out.println("==set successful!!");
        String value = jedisCluster.get("james:age");
        System.out.println(value);
        jedisCluster.close();
    }
} 