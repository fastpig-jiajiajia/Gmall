//package com.gmall.search.filter;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.gmall.entity.Payload;
//import com.gmall.entity.SysUser;
//import com.gmall.search.config.RsaKeyProperties;
//import com.gmall.util.JwtUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
///**
// * @author john
// * @date 2020/1/12 - 10:57
// */
//public class JwtVerifyFilter extends BasicAuthenticationFilter {
//    private RsaKeyProperties prop;
//
//    public JwtVerifyFilter(AuthenticationManager authenticationManager, RsaKeyProperties prop) {
//        super(authenticationManager);
//        this.prop = prop;
//    }
//
//    /**
//     * 过滤请求
//     */
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                    FilterChain chain) {
//        try {
//            //请求体的头中是否包含Authorization
//            System.out.println(request.getRequestURL());
//
//            String header = request.getHeader("Authorization");
//            header = request.getHeader("Authorization");
//
//            if(StringUtils.isBlank(header)){
//                Map<String, String[]> params = request.getParameterMap();
//                for (Map.Entry<String, String[]> param : params.entrySet()) {
//                    String key = param.getKey();  // 参数名
//                    String value = StringUtils.join(param.getValue());  // 参数值
//                    if("Authorization".equals(param.getKey())){
//                        header = StringUtils.join(param.getValue());
//                        break;
//                    }
//                }
//            }
//
//            //Authorization中是否包含Bearer，不包含直接返回
//            if (header == null || !header.startsWith("Bearer ")) {
//                // 重定向到登录页面
//                response.sendRedirect("http://localhost:8085/index?ReturnUrl=" + request.getRequestURL());
//                return;
//            }
//
//            //获取权限失败，会抛出异常
//            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
//            //获取后，将Authentication写入SecurityContextHolder中供后序使用
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            response.addHeader("Authorization", header);
//            System.out.println(request.getRequestURL().substring(21));
//
//            HttpServletResponse resp = (HttpServletResponse) response;
//            // 添加参数，允许任意domain访问
//            response.setHeader("Access-Control-Allow-Origin", "*");
//            // 这个allow-headers要配为*，这样才能允许所有的请求头 --- update by zxy  in 2018-10-19
//            response.setHeader("Access-Control-Allow-Headers", "*");
//            response.setHeader("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
//            response.setHeader("Access-Control-Max-Age", "1000 * 3600");
//        //    resp.sendRedirect(request.getRequestURL().toString());
//        //    request.getRequestDispatcher(request.getRequestURL().substring(21)).forward(request, response);
//        //    chain.doFilter(request, response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 未登录提示
//     *
//     * @param response
//     */
//    private void responseJson(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            //未登录提示
//        //    response.sendRedirect("http://localhost:8085/index?ReturnUrl=" + request.getRequestURL());
//            response.setContentType("application/json;charset=utf-8");
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            PrintWriter out = response.getWriter();
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("code", HttpServletResponse.SC_FORBIDDEN);
//            map.put("message", "请登录！");
//            out.write(new ObjectMapper().writeValueAsString(map));
//            out.flush();
//            out.close();
//
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//    }
//
//    /**
//     * 通过token，获取用户信息
//     *
//     * @param request
//     * @return
//     */
//    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
//        String token = request.getHeader("Authorization");
//        if (token != null) {
//            //通过token解析出载荷信息
//            Payload<SysUser> payload = JwtUtils.getInfoFromToken(token.replace("Bearer ", ""),
//                    prop.getPublicKey(), SysUser.class);
//            SysUser user = payload.getUserInfo();
//            //不为null，返回
//            if (user != null) {
//                return new UsernamePasswordAuthenticationToken(user, null, user.getRoles());
//            }
//            return null;
//        }
//        return null;
//    }
//}
