package com.gmall.jbasedemo.reflect.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-27 21:26:09
 * @description
 */
@Component
public class ReflectClsUtil {

    @Autowired
    private ClsCacheUtil cacheUtil;

    //jp.getTarget().getClass().getName();

    public User getUser(User user) {
        Class userCls = user.getClass();
        String className = this.getClass().getName();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        Map<String, Class> map = ClsCacheUtil.map.get(user.getClass().getName());

        User reflectUser = new User();
        List list = new ArrayList();
        map.forEach((k, v) -> {
            try {
                Field field = userCls.getDeclaredField(k);
                field.setAccessible(true);
                Field field1 = reflectUser.getClass().getDeclaredField(k);
                field1.setAccessible(true);
                field1.set(reflectUser, field.get(user));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

        });

        return reflectUser;
    }


    @PostConstruct
    public void addMap() {
//        Map fieldMap = new HashMap();
//        fieldMap.put("name", String.class);
//        fieldMap.put("age", Integer.class);
//        fieldMap.put("role", Role.class);
//
//        String className = this.getClass().getName();
//        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
//        ClsCacheUtil.map.put(User.class.getName(), fieldMap);
    }

}
