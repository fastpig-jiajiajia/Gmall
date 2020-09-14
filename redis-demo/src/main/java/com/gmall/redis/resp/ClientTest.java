package com.gmall.redis.resp;

import redis.clients.jedis.Jedis;

public class ClientTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        jedis.set("name","rehash");
        jedis.close();
    }
}
