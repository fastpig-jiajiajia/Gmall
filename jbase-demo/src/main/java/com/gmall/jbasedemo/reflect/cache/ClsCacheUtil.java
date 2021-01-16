package com.gmall.jbasedemo.reflect.cache;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-27 21:24:42
 * @description
 */
@Component
public class ClsCacheUtil {

    /**
     * String：类名
     * List：拦截的字段信息 Map
     * Map: K: 字段名称 V:字段类型
     */
    public static Map<String, Map<String, Class>> map = new ConcurrentHashMap<>();

    public static Map<String, Class> clsMap = new ConcurrentHashMap<>();

}
