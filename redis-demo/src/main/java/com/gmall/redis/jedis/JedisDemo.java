package com.gmall.redis.jedis;

import com.gmall.redis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Tuple;

import java.lang.reflect.Array;
import java.util.*;

import static com.sun.javafx.fxml.expression.Expression.add;

public class JedisDemo {
    /**
     * 可以设置中文键值对，但是redis-cli会乱码，jedis 不会；
     * 键值对可以都为 null 或者空字符串，
     * list 没有元素时就会被移除，keys * 无法查看
     */

    @Autowired
    private static Jedis jedis;

    public static void main(String[] args) {

        jedis = RedisUtil.getJedis();


//        System.out.println("Redis String  =========================");
//        stringMethod();
//
//        System.out.println("Redis Common  =========================");
//        commonMethod();
//
//        System.out.println("Redis Hash  =========================");
//        hashMethod();

//        System.out.println("Redis List  =========================");
//        listMethod();

        System.out.println("Redis Set  =========================");
        zsetMethod();

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

    /**
     * set 命令
     */
    private static void setMethod() {
        // 设置成功 返回插入成功的数量，已经存在的 kye 不计入插入成功数量
        System.out.println(jedis.sadd("gmall-set", new String[]{"1", "2", "3"}));
        List<String> setValues = new ArrayList<String>() {{
            add("1");
            add("2");
            add("3");
        }};
        System.out.println("set 插入数据数量：" + jedis.sadd("gmall-set-1", setValues.toArray(new String[setValues.size()])));
        System.out.println("set 插入数据数量：" + jedis.sadd("gmall-set-1", setValues.toArray(new String[setValues.size()])));
        System.out.println("set 插入单个数据：" + jedis.sadd("gmall-set-1", "4"));
        System.out.println("set 插入重复数据：" + jedis.sadd("gmall-set-1", "4"));

        System.out.println("set 插入数据数量：" + jedis.sadd("gmall-set-2", setValues.toArray(new String[setValues.size()])));
        System.out.println("set 插入单个数据：" + jedis.sadd("gmall-set-2", "5"));
        System.out.println("set 插入单个数据：" + jedis.sadd("gmall-set-2", "6"));

        jedis.sadd("gmall-set-3", setValues.toArray(new String[setValues.size()]));
        jedis.sadd("gmall-set-3", "4");

        jedis.sadd("gmall-set-3", "7");


        // 获取集合所有的成员，返回 set
        System.out.println("获取所有成员：" + jedis.smembers("gmall-set-1"));

        // 判断set 中是否存在指定元素
        System.out.println("set 存在元素：" + jedis.sismember("gmall-set-1", "4"));
        System.out.println("set 不存在元素：" + jedis.sismember("gmall-set-1", "999"));

        // 获取所有指定 set 的交集，可以指定多个，使用数组的形式，为空时返回空 Set 集合
        System.out.println("获取交集：" + jedis.sinter("gmall-set-1", "gmall-set-2"));
        System.out.println("获取交集为空：" + jedis.sinter("gmall-set-1", "gmall-set-4"));

        // 获取第一个集合和其他集合的 set 的差异，第一个集合独有的，为空时返回空 Set 集合
        System.out.println("获取差集：" + jedis.sdiff("gmall-set-1", "gmall-set-2"));
        System.out.println("获取差集为空：" + jedis.sdiff("gmall-set-1", "gmall-set-3"));

        // 获取给定所有集合的并集，为空时返回空 Set 集合
        System.out.println("获取并集：" + jedis.sinter("gmall-set-1", "gmall-set-2"));
        System.out.println("获取并集为空：" + jedis.sinter("gmall-set-5", "gmall-set-6"));

        // 删除 set 集合中的某个元素，返回成功删除的数量，不存在的value不会被统计到
        System.out.println("删除元素：" + jedis.srem("gmall-set-3", "1", "2"));
        System.out.println("删除元素：" + jedis.srem("gmall-set-5", "1", "2"));
        System.out.println("获取所有成员：" + jedis.smembers("gmall-set-3"));

        // 随机移除 set 中的一个元素，并将此元素从 set 中删除，set不存在时返回 null
        System.out.println("随机获取一个元素并删除：" + jedis.spop("gmall-set-3"));
        System.out.println("获取所有成员：" + jedis.smembers("gmall-set-3"));
        System.out.println("随机获取一个元素并删除：" + jedis.spop("gmall-set-5"));  // set 不存在，返回 null

        // 随机返回指定数量的元素
        System.out.println("随机返回指定数量的元素：" + jedis.srandmember("gmall-set-1", 1));

    }

    /**
     * zset 命令
     */
    private static void zsetMethod() {
        // 设置成功 返回插入成功的数量，member 存在时不计入插入成功数量，即使score不同
        Map<String, Double> zsetMap = new HashMap<String, Double>() {{
            put("xiaohong", new Double(80));
            put("xiaoming", new Double(100));
            put("xiaolan", new Double(110));
            put("xiaotong", new Double(130));
        }};
        System.out.println("zset 插入数据数量：" + jedis.zadd("gmall-zset-1", zsetMap));
        System.out.println("zset 插入数据数量：" + jedis.zadd("gmall-zset-1", 120, "xiaogang"));

        // 获取 zset 指定成员分数
        System.out.println("zset 获取指定成员分数：" + jedis.zscore("gmall-zset-1", "xiaoming"));

        // 获取 zset 成员数量
        System.out.println("zset 成员数量：" + jedis.zcard("gmall-zset-1"));

        // 返回指定 score 区间的成员数量
        System.out.println("zset 区间成员数量：" + jedis.zcount("gmall-zset-1", 95, 105));

        // 对指定的 members 的 score进行加减分
        System.out.println("zset member 加分：" + jedis.zincrby("gmall-zset-1", 1, "xiaoming"));
        System.out.println("zset 加分 score：" + jedis.zscore("gmall-zset-1", "xiaoming"));
        System.out.println("zset member 减分：" + jedis.zincrby("gmall-zset-1", -1, "xiaoming"));
        System.out.println("zset 减分 score：" + jedis.zscore("gmall-zset-1", "xiaoming"));

        // 在有序集合中计算指定字典区间内成员数量，必须带 [
        System.out.println("zset 获取满足条件的 members：" + jedis.zlexcount("gmall-zset-1", "[xiaoh", "[xiaom"));

        // 根据索引获取用户，即搜索排名第几到第几的 member, 从 0 开始算第一
        System.out.println("zset 获取所有成员：" + jedis.zrange("gmall-zset-1", 0, -1)); // 返回所有 member
        System.out.println("zset 分数从低到高，获取排名第一到第二成员：" + jedis.zrange("gmall-zset-1", 0, 1));
        System.out.println("zset 分数从高到低，获取排名第一到第二成员：" + jedis.zrevrange("gmall-zset-1", 0, 1));

        // 返回 member && score，遍历
        Set<Tuple> tupleSet = jedis.zrangeWithScores("gmall-zset-1", 0, -1);
        tupleSet.forEach(t -> {
            System.out.println("member: " + t.getElement());
            System.out.println("score: " + t.getScore());
        });

        // 根据指定 score 范围获取成员
        System.out.println("zset 根据指定 score 范围从低到高获取成员：" + jedis.zrangeByScore("gmall-zset-1", 90, 110));
        System.out.println("zset 根据指定 score 范围从高到低获取成员：" + jedis.zrevrangeByScore("gmall-zset-1", 90, 110));

        // 获取指定member 的排序索引，就是排第几，正序和倒序，真正的排名需要 +1
        System.out.println("zset 按分数从低到高排第：" + (jedis.zrank("gmall-zset-1", "xiaoming") + 1));
        System.out.println("zset 按分数从高到低排第：" + (jedis.zrevrank("gmall-zset-1", "xiaoming") + 1));

        // 移除 memmber，返回刪除成功 member 的数量
        System.out.println("zset 移除指定的 member：" + jedis.zrem("gmall-zset-1", "xiaohong"));
        System.out.println("zset 获取所有成员：" + jedis.zrange("gmall-zset-1", 0, -1)); // 返回所有 member
        System.out.println("zset 移除指定member排名区间的 member：" + jedis.zremrangeByRank("gmall-zset-1", 0, 1));
        System.out.println("zset 获取所有成员：" + jedis.zrange("gmall-zset-1", 0, -1)); // 返回所有 member
        System.out.println("zset 移除指定分数排名区间的 member：" + jedis.zremrangeByScore("gmall-zset-1", 100, 120));
        System.out.println("zset 获取所有成员：" + jedis.zrange("gmall-zset-1", 0, -1)); // 返回所有 member
        System.out.println("zset 移除指定分数排名区间的 member：" + jedis.zremrangeByLex("gmall-zset-1", "[xiao ", "[xiao~"));
//        System.out.println("zset 移除指定分数排名区间的 member：" + jedis.zremrangeByLex("gmall-zset-1", "[xiao", "[xiao"));
        System.out.println("zset 获取所有成员：" + jedis.zrange("gmall-zset-1", 0, -1)); // 返回所有 member


    }

}
