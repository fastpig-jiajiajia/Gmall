package com.gmall.redis.piepline;

import redis.clients.jedis.Jedis;

/**
 * 初始化数据，批量设值
 */
public class RedisTools {
    public static int arraylength = 10000;
    public static String ip = "39.101.198.56";
    public static int port = 6379;
    public static String[] keys = new String[arraylength / 2];

    /**
     * 初始化数据，批量设值
     * redis提供的批量设值mset  批量取值 mget，但没有批量删除mdel指令
     */
    //String[]{"key:0","v0","key:1","v1","key:2","v2","key:3","v3","key:4","v4"......."key:4999","v:4999"})
    public static void initRedisData() {
        Jedis jedis = new Jedis(ip, port);
        String[] str = new String[arraylength]; //10000

        int j = 0;
        for (int i = 0; i < str.length / 2; i++) {
            str[j] = "key:" + i;
            str[j + 1] = "v" + i;
            j = j + 2;

            keys[i] = "key:" + i;
        }
        jedis.mset(str);
        jedis.close();
    }
}
