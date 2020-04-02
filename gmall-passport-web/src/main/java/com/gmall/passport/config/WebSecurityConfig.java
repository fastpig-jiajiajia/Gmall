//package com.gmall.passport.config;
//
//import com.gmall.passport.filter.JwtLoginFilter;
//import com.gmall.passport.filter.JwtVerifyFilter;
//import com.gmall.passport.service.UserDetailServiceImpl;
//import com.gmall.service.IUserDetailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
///**
// * @author Administrator
// * @version 1.0
// **/
//@Configuration
//@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    /**
//     * 自定以成功处理器
//     */
//    @Autowired
//    private CommonAuthenticationSuccessHandler authenticationSuccessHandler;
//
//    @Autowired
//    private RsaKeyProperties prop;
//
//    @Autowired
//    IUserDetailService userDeatailService;
//
//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
////    //认证管理器
////    @Bean
////    public AuthenticationManager authenticationManagerBean() throws Exception {
////        return super.authenticationManagerBean();
////    }
//    //密码编码器
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////    }
//    @Bean
//    public BCryptPasswordEncoder myPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    //安全拦截机制（最重要）
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests()
//                .antMatchers("/static/**").permitAll()
//                .antMatchers("/css/**").permitAll()
//                .antMatchers("/js/**").permitAll()
//                .antMatchers("/img/**").permitAll()
//                .antMatchers("/index").permitAll()
//                .antMatchers("/login*").permitAll()
//                .antMatchers("/vlogin").permitAll()
//                .antMatchers("/logout").permitAll()
//                .anyRequest().authenticated()
//                .and().formLogin().successHandler(this.authenticationSuccessHandler)
//                .and()
//                //增加自定义认证过滤器
//                .addFilter(new JwtLoginFilter(authenticationManager(), prop))
//                //增加自定义验证认证过滤器
//                .addFilter(new JwtVerifyFilter(authenticationManager(), prop))
//                // 前后端分离是无状态的，不用session了，直接禁用。
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        /**
//         * loginPage()： 自定义登录页面
//         loginProcessingUrl()：将用户名和密码提交到的URL
//         defaultSuccessUrl()： 成功登录后跳转的URL。 如果是直接从登录页面登录，会跳转到该URL；如果是从其他页面跳转到登录页面，登录后会跳转到原来页面。可设置true来任何时候到跳转该URL。
//         successForwardUrl()：成功登录后重定向的URL
//         failureUrl()：登录失败后跳转的URL，指定的路径要能匿名访问
//         failureForwardUrl()：登录失败后重定向的URL
//         */
//    }
//
//
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        //UserDetailsService类
//        auth.userDetailsService(userDeatailService)
//                //加密策略
//                .passwordEncoder(bCryptPasswordEncoder);
//    }
//
//
//}
