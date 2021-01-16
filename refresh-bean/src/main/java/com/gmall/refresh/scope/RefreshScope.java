package com.gmall.refresh.scope;

import lombok.Data;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RefreshScope implements Scope {

    public static volatile Map beanMap = new ConcurrentHashMap();

    /**
     * 自定义 Scope，对这个配置了这个作用域的 Bean进行获取时，走的就是这里的逻辑
     * 如果自定义的缓存中已经存在了 Bean，那么就从 map中获取Bean，否则就重新创建Bean
     *
     * @param beanName
     * @param objectFactory
     * @return
     */
    @Override
    public Object get(String beanName, ObjectFactory<?> objectFactory) {
        if (beanMap.containsKey(beanName)) {
            return beanMap.get(beanName);
        }

        Object object = objectFactory.getObject();
        beanMap.put(beanName, object);
        return object;
    }

    /**
     * 这个作用域，调用Bean的移除操作时，就会调用到这里
     * 这里的逻辑就是从自定义的 Bean缓存Map中移除 Bean信息
     *
     * @param beanName
     * @return
     */
    @Override
    public Object remove(String beanName) {
        return beanMap.remove(beanName);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
}
