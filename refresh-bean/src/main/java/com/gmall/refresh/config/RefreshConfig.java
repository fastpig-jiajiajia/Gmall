package com.gmall.refresh.config;

import com.gmall.refresh.constant.RefreshConstant;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rui.xu
 * @date 2020/12/04 10:29
 * @description
 **/
@Data
@Scope(RefreshConstant.SCOPE_NAME)
@Configuration
public class RefreshConfig {

    public static Map<String, String> refreshConfigMap;

    @Value("${config.webPath:null}")
    private String webPath;

    @Value("${refresh.zk.path}")
    private String path;

    @PostConstruct
    public void initMap() {
        Map<String, String> pathMap = new ConcurrentHashMap<String, String>() {
            {
                put("order.orderNo", "202012041045");
                put("order.orderAmount", "19.8");
                put("order.orderContent", "线下交易");

                put("web.uri", "202012041045");
                put("web.excludeUri", "20201206155");
                put("config.webPath", "/login");

                put("spring.datasource.url", "jdbc:mysql://rm-8vbsw8p959dm7052wro.mysql.zhangbei.rds.aliyuncs.com:3306/acl-control?allowMultiQueries=true&serverTimezone=Asia/Shanghai");
                put("spring.datasource.username", "root_x");
                put("spring.datasource.password", "Xr20200101!");
            }
        };

        addPrefix(pathMap);
    }

    /**
     * 组成 zookeeper节点
     *
     * @param pathMap
     */
    private void addPrefix(Map<String, String> pathMap) {
        refreshConfigMap = new ConcurrentHashMap<>();
        if (!RefreshConstant.PREFIX.equals(path.substring(0, 1))) {
            this.path = RefreshConstant.PREFIX + this.path;
        }
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            String key = this.path + RefreshConstant.PREFIX + entry.getKey();
            String value = entry.getValue();
            refreshConfigMap.put(key, value);
        }
    }
}