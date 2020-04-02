//package com.gmall.passport.config;
//
//import com.alibaba.dubbo.config.annotation.Reference;
//import com.gmall.service.UserService;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
//import org.springframework.security.web.savedrequest.RequestCache;
//import org.springframework.security.web.savedrequest.SavedRequest;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.concurrent.Executor;
//
///**
// * 登录成功后的处理策略
// */
//@Component
//public class CommonAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//    @Reference
//    private UserService userService;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
//        try {
//            // 登录成功后，进行数据处理
//            System.out.println("用户登录成功啦！！！");
//            System.out.println("用户登录信息打印 getPrincipal：" + authentication.getPrincipal());
//            System.out.println("用户登录信息打印 getName：" + authentication.getName());
//            System.out.println("用户登录信息打印 getAuthorities：" + authentication.getAuthorities());
//            System.out.println("用户登录信息打印 getDetails：" + authentication.getDetails());
//            System.out.println("用户登录信息打印 getCredentials：" + authentication.getCredentials());
//
//            //处理完成后，跳转回原请求URL
//            response.setContentType("application/json;charset=utf-8");
//            RequestCache cache = new HttpSessionRequestCache();
//            System.out.println(request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST"));
//            SavedRequest savedRequest = cache.getRequest(request, response);
//            String url = "";
//            if(savedRequest != null){
//                url = savedRequest.getRedirectUrl();
//            }
//            if(StringUtils.isBlank(url)){
//                getRedirectStrategy().sendRedirect(request,response,"http://localhoost:8083/index");
//            }
//
//        //    response.sendRedirect(url);
//
//            super.onAuthenticationSuccess(request, response, authentication);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
