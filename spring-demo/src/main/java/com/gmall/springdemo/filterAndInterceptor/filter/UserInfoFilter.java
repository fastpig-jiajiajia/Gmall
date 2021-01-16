package com.gmall.springdemo.filterAndInterceptor.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

//@WebFilter(filterName = "ControllerTimeFilter2", urlPatterns = {"/**"})
@Component
public class UserInfoFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("UserInfoFilter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("UserInfoFilter doFilter");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        System.out.println("UserInfoFilter destroy");
    }
}
