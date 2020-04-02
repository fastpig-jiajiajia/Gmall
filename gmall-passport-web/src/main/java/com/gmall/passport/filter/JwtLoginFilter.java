//package com.gmall.passport.filter;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.gmall.entity.SysRole;
//import com.gmall.entity.SysUser;
//import com.gmall.passport.config.RsaKeyProperties;
//import com.gmall.util.JwtUtils;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.xml.bind.SchemaOutputResolver;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author john
// * @date 2020/1/12 - 10:26
// */
//public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
//    private AuthenticationManager authenticationManager;
//    private RsaKeyProperties prop;
//
//    // 过滤器自动注入，所以由构造器去赋值
//    public JwtLoginFilter(AuthenticationManager authenticationManager, RsaKeyProperties prop) {
//        this.authenticationManager = authenticationManager;
//        this.prop = prop;
//    }
//
//    //重写springsecurity获取用户名和密码操作
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        try {
//            //从输入流中获取用户名和密码，而不是表单
//            System.out.println(request.getRequestURL());
//            System.out.println(request.getParameter("username"));
//            System.out.println(request.getParameter("password"));
//            System.out.println(request.getParameter("returnUrl"));
//            System.out.println(request.getInputStream());
//            SysUser sysUser = new SysUser();
//            if(request.getInputStream() != null && "".equals(request.getInputStream()) ){
//                 sysUser = new ObjectMapper().readValue(request.getInputStream(), SysUser.class);
//            }else{
//                sysUser.setUsername(request.getParameter("username"));
//                sysUser.setPassword(request.getParameter("password"));
//            }
//
//            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(sysUser.getUsername(), sysUser.getPassword());
//            return authenticationManager.authenticate(authRequest);
//        } catch (Exception e) {
//            try {
//                //处理失败请求
////                response.setContentType("application/json;charset=utf-8");
////                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////                PrintWriter out = response.getWriter();
////                Map map = new HashMap<>();
////                map.put("code", HttpServletResponse.SC_UNAUTHORIZED);
////                map.put("msg", "用户名或者密码错误");
////                out.write(new ObjectMapper().writeValueAsString(map));
////                out.flush();
////                out.close();
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//
//            throw new RuntimeException(e);
//        }
//    }
//
//    // 登陆成功的方法，重写用户名密码授权成功操作----返回token凭证
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        //从authResult获取认证成功的用户信息
//        System.out.println(request.getRequestURL());
//        System.out.println(request.getParameter("username"));
//        System.out.println(request.getParameter("password"));
//        System.out.println(request.getParameter("returnUrl"));
//
//
//        SysUser resultUser = new SysUser();
//        SysUser authUser = (SysUser) authResult.getPrincipal();
//
//        // 重新封装对象，生成 token
//        resultUser.setUsername(authUser.getUsername());
//        resultUser.setId(authUser.getId());
//        resultUser.setStatus(authUser.getStatus());
//        resultUser.setRoles((List<SysRole>) authResult.getAuthorities());
//        try {
//            //登录成功時，携带 token 重定向回原页面
//            String token = JwtUtils.generateTokenExpireInMinutes(resultUser, prop.getPrivateKey(), 3600*24);  // 有效时间
//            //将token写入header
//            response.addHeader("Access-Control-Allow-Origin", "*");
//            response.addHeader("Authorization", "Bearer " + token);
//            response.setContentType("application/json;charset=utf-8");
//            response.setStatus(HttpServletResponse.SC_OK);
//            System.out.println("Authorization : " + response.getHeader("Authorization"));
//            response.sendRedirect(request.getParameter("returnUrl") + "?Authorization=" + "Bearer " + token);
//        //    request.getRequestDispatcher(request.getParameter("returnUrl")).forward(request,response);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//    }
//}
