package com.gmall.refresh.configuration;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author rui.xu
 * @date 2020/12/04 10:54
 * @description
 **/
@Configuration
public class CuratorFrameworkConfiguration {

    @Value("${zookeeper.url}")
    private String url;

    @Value("${zookeeper.connectionTimeoutMs}")
    private Integer connectionTimeoutMs;

    @Value("${zookeeper.sessionTimeoutMs}")
    private Integer sessionTimeoutMs;

    @Bean
    public CuratorFramework getCuratorFramework() {
        return CuratorFrameworkFactory.
                builder().
                connectString(url).
                connectionTimeoutMs(connectionTimeoutMs).  // 连接超时时间
                sessionTimeoutMs(sessionTimeoutMs).  // 会话超时时间
                retryPolicy(new ExponentialBackoffRetry(1000, 3)). // 刚开始重试间隔为1秒，之后重试间隔逐渐增加，最多重试不超过三次
                build();
    }
}