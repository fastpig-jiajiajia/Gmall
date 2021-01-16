package com.gmall.springdemo.filterAndInterceptor.configuration;

import com.gmall.springdemo.filterAndInterceptor.interceptor.UserInfoInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class InterceptorConfiguration1 implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 实现WebMvcConfigurer不会导致静态资源被拦截
        registry.addInterceptor(new UserInfoInterceptor()).addPathPatterns("/**");
    }
}