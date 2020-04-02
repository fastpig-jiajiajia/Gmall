//package com.gmall.passport.filter;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.gmall.entity.Payload;
//import com.gmall.entity.SysUser;
//import com.gmall.passport.config.RsaKeyProperties;
//import com.gmall.util.JwtUtils;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.PrintWriter;
//import java.util.HashMap;
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
//            String header = request.getHeader("Authorization");
//            //Authorization中是否包含Bearer，不包含直接返回
//            if (header == null || !header.startsWith("Bearer ")) {
//                chain.doFilter(request, response);
////                responseJson(response);
//                return;
//            }
//            //获取权限失败，会抛出异常
//            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
//            //获取后，将Authentication写入SecurityContextHolder中供后序使用
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            chain.doFilter(request, response);
//        } catch (Exception e) {
////            responseJson(response);
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 未登录提示
//     *
//     * @param response
//     */
//    private void responseJson(HttpServletResponse response) {
//        try {
//            //未登录提示
//            response.setContentType("application/json;charset=utf-8");
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            PrintWriter out = response.getWriter();
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("code", HttpServletResponse.SC_FORBIDDEN);
//            map.put("message", "请登录！");
//            out.write(new ObjectMapper().writeValueAsString(map));
//            out.flush();
//            out.close();
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//    }
//
//    /**
//     * 通过token，获取用户信息
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
