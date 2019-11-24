package com.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.gmall.annotations.LoginRequired;
import com.gmall.util.CookieUtil;
import com.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取被拦截的请求的访问的方法的注解，是否配置 LoginRequired
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequired loginRequired = handlerMethod.getMethodAnnotation(LoginRequired.class);

        // 没有配置注解就不需要登录
        if(loginRequired == null){
            return true;
        }

        // 获取 token
        String token = "";

        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if(StringUtils.isNotBlank("oldToken")){
            token = oldToken;
        }

        String newToken = request.getParameter("token");
        if(StringUtils.isNotBlank(newToken)){
            token = newToken;
        }

        // 调用passport 认证中心根据 token 进行认证
        String success = "fail";
        Map<String,String> successMap = new HashMap<>();
        if(StringUtils.isNotBlank(token)){
            String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
            if(StringUtils.isBlank(ip)){
                ip = request.getRemoteAddr();// 从request中获取ip
                if(StringUtils.isBlank(ip)){
                    ip = "127.0.0.1";
                }
            }

            // 必须传递ip过去，否则认证中心的 request 获取的只是拦截器所在服务器的地址
            String successJson  = HttpclientUtil.doGet("http://passport.gmall.com:8085/verify?token=" + token + "&currentIp=" + ip);

            successMap = JSON.parseObject(successJson, Map.class);

            success = successMap.get("status");
        }

        // 得到发起请求的url，作为登陆成功后的跳转页面
        String returnUrl = request.getRequestURL().toString();
        if(loginRequired.loginSuccess()){
            // 认证中心认证成功
            if ("success".equals(success)) {
                // 需要将token携带的用户信息写入
                request.setAttribute("memberId", successMap.get("memberId"));
                request.setAttribute("nickname", successMap.get("nickname"));
                CookieUtil.setCookie(request, response, "oldToken", token, 60*60*72, true);
                return true;
            }

            response.sendRedirect("http://passport.gmall.com:8085/index?ReturnUrl=" + returnUrl);
            return false;
        }else{
            // 已经是登录状态，覆写cookie token
            if ("success".equals(success)) {
                // 需要将token携带的用户信息写入
                request.setAttribute("memberId", successMap.get("memberId"));
                request.setAttribute("nickname", successMap.get("nickname"));
                if(StringUtils.isNotBlank(token)){
                    CookieUtil.setCookie(request, response, "oldToken", token, 60*60*72, true);
                }
            }
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
