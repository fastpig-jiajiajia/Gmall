//package com.gmall.gmallredissontest.config;
//
//import org.redisson.Redisson;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.Config;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import java.io.IOException;
//
///**
// * redisson配置
// */
//@Configuration
//public class RedssionConfig {
//
//    @Value("${spring.redis.cluster.nodenames}")
//    private String cluster;
//    @Value("${spring.redis.password}")
//    private String password;
//
//    @Bean
//    @Primary
//    public RedissonClient getRedisson(){
//        String[] nodes = cluster.split(",");
//        // redisson 版本是3.10，集群的ip前面要加上“redis://”，不然会报错，3.2版本可不加
//        for(int i = 0, size = nodes.length; i < size; i++){
//            nodes[i] = "redis://" + nodes[i];
//        }
//        RedissonClient redisson = null;
//        Config config = new Config();
//        config.useClusterServers() //这是用的集群server
//                .setScanInterval(2000) //设置集群状态扫描时间
//                .addNodeAddress("redis://39.101.198.56:6379")
//                .setPassword(password);
//        redisson = Redisson.create(config);
//
//        //可通过打印redisson.getConfig().toJSON().toString()来检测是否配置成功
//        try {
//            System.out.println(redisson.getConfig().toJSON().toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return redisson;
//    }
//}
