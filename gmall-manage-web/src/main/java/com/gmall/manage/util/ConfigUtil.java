package com.gmall.manage.util;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {

    private static Properties properties = new Properties();

    //  无参数默认初始化 application.properties
    public ConfigUtil() {
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 根据 key 获取 value，默认 application.properties
    public static String getConfigValue(String key) {
        return properties.getProperty(key);
    }

    // 设置 value，默认 application.properties
    public static void updateConfigValue(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * 获取指定 properties 的 key 的值
     *
     * @param key
     * @param propertiesName
     * @return
     */
    public static String getConfigValue(String key, String propertiesName) {
        String value = null;
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesName));
            value = properties.getProperty(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 设置指定 properties 的键值对
     * @param key
     * @param value
     * @param propertiesName
     */
    public static void updateConfigValue(String key, String value, String propertiesName) {
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesName));
            properties.setProperty(key, value);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
