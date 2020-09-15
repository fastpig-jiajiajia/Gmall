package com.gmall.redis.jedis;

import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;

public class JedisDemo {
    /**
     * 可以设置中文键值对，但是redis-cli会乱码，jedis 不会；
     * 键值对可以都为 null 或者空字符串，
     * list 没有元素时就会被移除，keys * 无法查看
     */

    @Autowired
    private static JedisCluster jedis;

    public static void main(String[] args) {

//        System.out.println("Redis String  =========================");
//        stringMethod();
//
//        System.out.println("Redis Common  =========================");
//        commonMethod();
//
//        System.out.println("Redis Hash  =========================");
//        hashMethod();

        System.out.println("Redis List  =========================");
        listMethod();

    }

    /**
     * 通用命令
     */
    private static void commonMethod() {
        // key 是否存在，存在 1， 不存在 0
        System.out.println(jedis.exists("redis-set-ex-nx"));

        // 为 key 设置过期时间，成功 1，失败 0  EXPIRE key seconds， 毫秒 PEXPIRE key milliseconds
        System.out.println(jedis.expire("redis-set-ex-nx", 11));

        // 查看 key 的剩余时间, 秒级 TTL key，毫秒 PTTL key
        System.out.println(jedis.ttl("redis-set-ex-nx"));

        // 移除 key 的过期时间，永久保存，而不是就立即删除 成功 1， 失败 0， PERSIST key
        System.out.println(jedis.persist("redis-set-ex-nx"));

        // 查看 key 的类型，TYPE key，key 不存在返回 none
        System.out.println(jedis.type("redis-set-ex-nx"));

        // 重命名 key, RENAME key newkey 成功 OK，失败抛出异常
        System.out.println(jedis.rename("redis-set-ex-nx", "redis-set-ex-nx-1"));

        // 删除 key DEL key, 返回删除key 的数量
        System.out.println(jedis.del("redis-set-ex-nx-1"));
    }


    /**
     * String 命令
     */
    private static void stringMethod() {
        // 设置成功 OK
        System.out.println(jedis.set("redis-demo-set", "男"));
        System.out.println(jedis.get("redis-demo-set"));

        // 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以秒为单位) SETEX key seconds value, 成功 返回 OK
        System.out.println(jedis.setex("redis-set", 10, "redis-set"));

        // 只有在 key 不存在时设置 key 的值。  SETNX key value, 成功 1，失败 0
        System.out.println(jedis.setnx("redis-set", "redis-set"));

        // 只有在 key 不存在时设置 key 值, 并设置过期时间 ex 秒为单位，px 毫秒为单位, 成功 OK， 失败 null
        System.out.println(jedis.set("redis-set-ex-nx", "values", "nx", "ex", 10));
    }


    /**
     * Hash 命令
     */
    private static void hashMethod() {
        // 设置成功 1, 值无变动 0
        System.out.println(jedis.hset("gmall:user:xr", "name", "徐锐"));
        System.out.println(jedis.hset("gmall:user:xr", "age", "23"));

        // 获取指定字段值
        System.out.println(jedis.hget("gmall:user:xr", "name"));
        // 获取 key 下所有值
        System.out.println(jedis.hgetAll("gmall:user:xr"));

        // 获取所有字段 	HKEYS key
        System.out.println(jedis.hkeys("gmall:user:xr"));

        // 查看 Hash 表中字段是否存在，HEXISTS key field，存在 1，不存在0
        System.out.println(jedis.hexists("gmall:user:xr", "name"));

        // 只有在字段 field 不存在时，设置哈希表字段的值。  HSETNX key field value, 成功 1，失败 0
        System.out.println(jedis.hsetnx("gmall:user:xr", "sex", "1"));

        // 为哈希表 key 中的指定字段的整数值加上增量 increment, HINCRBY key field increment 返回增加完的值
        System.out.println(jedis.hincrBy("gmall:user:xr", "age", 2));

    }


    /**
     * List 命令
     */
    private static void listMethod() {
        // 设置成功 返回列表的长度
        System.out.println(jedis.lpush("gmall-queue", "1"));
        System.out.println(jedis.rpush("gmall-queue", "5"));

        // 取出值，没有值为 null
        System.out.println(jedis.lpop("gmall-queue"));
        System.out.println(jedis.rpop("gmall-queue"));
        System.out.println(jedis.lpop("gmall-queue"));

        // 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止，秒为单位 BLPOP key1 [key2 ] timeout，
        // 空为一个空数组，其他的为一个键值对
//        System.out.println(jedis.blpop("gmarll-queue", "100"));
//        System.out.println(jedis.brpop("gmall-queue", "100"));
        System.out.println(jedis.rpush("gmall-queue", "5"));

        // 获取长度
        System.out.println(jedis.llen("gmall-queue"));

        // 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
        // BRPOPLPUSH source destination timeout
        System.out.println(jedis.brpoplpush("gmall-queue", "gmall-queue-1", 100));


    }


}
