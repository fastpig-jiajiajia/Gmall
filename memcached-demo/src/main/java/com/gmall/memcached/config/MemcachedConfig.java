package com.gmall.memcached.config;

        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.stereotype.Component;

/**
 * memcached 配置类
 */
@Component
@ConfigurationProperties(prefix = "memcache")  // yml 文件一样操作
public class MemcachedConfig {

    private String ip;

    private int port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
