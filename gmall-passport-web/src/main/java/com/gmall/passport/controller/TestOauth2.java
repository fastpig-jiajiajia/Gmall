package com.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.gmall.GmallPassportWebApplication;
import com.gmall.util.HttpclientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

public class TestOauth2 {

    @Value("${weibo.appKey.client_id}")
    private static String clientId = "546561297";
    @Value("${weibo.appSecret.client_secret}")
    private static String clientSecret = "b5bcf0f702e0c1e96add1a0423bca694";
    @Value("${weibo.redirect_uri}")
    private static String redirectUri = "http://passport.gmall.com:8085/vlogin";
    @Value("${weibo.grant_type}")
    private static String grantType = "authorization_code";


    public static String getCode(){

        // 1 获得授权码
        // 187638711
        // http://passport.gmall.com:8085/vlogin

        String s1 = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id="+ clientId + "&response_type=code&redirect_uri=" + redirectUri);

        System.out.println(s1);

        // 在第一步和第二部返回回调地址之间,有一个用户操作授权的过程

        // 2 返回授权码到回调地址

        return s1;
    }

    /**
     * 获取token和 Uid
     * @return
     */
    public static String getAccess_token(){
        // 3 换取access_token
        // client_secret=a79777bba04ac70d973ee002d27ed58c
        String s3 = "https://api.weibo.com/oauth2/access_token?";//?client_id=187638711&client_secret=a79777bba04ac70d973ee002d27ed58c&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id", clientId);
        paramMap.put("client_secret", clientSecret);
        paramMap.put("grant_type", grantType);
        paramMap.put("redirect_uri", redirectUri);
        paramMap.put("code","188f3ea7780ea4f93fdf8c3b71e6ee44");// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);

        Map<String,String> access_map = JSON.parseObject(access_token_json,Map.class);

        System.out.println(access_map.get("access_token"));
        System.out.println(access_map.get("uid"));

        return access_map.get("access_token");
    }

    public static Map<String,String> getUser_info(){

        // 4 用access_token查询用户信息
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=2.00nXnnaG0nY_za4d7f2761a5JxvlHD&uid=6040613611";
        String user_json = HttpclientUtil.doGet(s4);
        Map<String,String> user_map = JSON.parseObject(user_json,Map.class);

        System.out.println(user_map.get("1"));

        return user_map;
    }


    public static void main(String[] args) {
    //    SpringApplication.run(TestOauth2.class, args);

        getCode();
    //    getAccess_token();
    //    getUser_info();
    }
}
