package com.gmall.refresh.controller;

import com.gmall.refresh.config.RefreshConfig;
import com.gmall.refresh.constant.RefreshConstant;
import com.gmall.refresh.service.RefreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RunAs;

/**
 * @author rui.xu
 * @date 2020/12/03 17:12
 * @description
 **/
@Scope(RefreshConstant.SCOPE_NAME)
@RestController
public class OrderController {

    @Value("${order.orderNo}")
    private String orderNo;

    @Autowired
    private RefreshConfig config;

    @Autowired
    private RefreshService service;

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @GetMapping("/queryContent/{id}")
    public String queryContent(@PathVariable String id){
        System.out.println(config.getWebPath());
        configurableApplicationContext.getEnvironment().getSystemEnvironment();  // 系统环境配置
        configurableApplicationContext.getEnvironment().getPropertySources();  // 所有扫描进来的配置文件
        configurableApplicationContext.getEnvironment().getSystemProperties();  // 系统环境配置
        configurableApplicationContext.getEnvironment().getActiveProfiles();  // spring.profile.active，mvn -P(dev)指定的值，不是配置文件，
        configurableApplicationContext.getEnvironment().getDefaultProfiles();

        applicationContext.getEnvironment().getActiveProfiles();
        applicationContext.getEnvironment().getDefaultProfiles();
        environment.getActiveProfiles();

        PropertySource propertySource = configurableApplicationContext.getEnvironment().getPropertySources().get(RefreshConstant.ZK_PROPERTY_NAME);
        service.doService();
        System.out.println("Controller: " + orderNo);

        return orderNo;
    }
}