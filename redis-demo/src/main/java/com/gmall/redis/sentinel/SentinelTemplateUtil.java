package com.gmall.redis.sentinel;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SentinelTemplateUtil {

    // spring 对redis操作的封装，使用了模板模式
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private Logger logger = LoggerFactory.getLogger("SentinelTemplateUtil");


    public void set(final String key, final String value) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                connection.set(
                        redisTemplate.getStringSerializer().serialize(key),
                        redisTemplate.getStringSerializer().serialize(value));
                logger.debug("save key:" + key + ",value:" + value);
                return null;
            }
        });
    }

    public String get(final String key) {
        @SuppressWarnings("rawtypes")
        BoundValueOperations<String, Object> bvo = redisTemplate.boundValueOps(key);
        return bvo.get().toString();

    }

    public void delete(final String key) {
        redisTemplate.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) {
                connection.del(redisTemplate.getStringSerializer().serialize(
                        key));
                return null;
            }
        });
    }


}  