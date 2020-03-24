package com.gmall.user.config;

import com.gmall.user.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * EnableWebSecurity注解使得SpringMVC集成了Spring Security的web安全支持
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 权限配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 配置拦截规则
        http.authorizeRequests().antMatchers("/static/**").permitAll()
                .antMatchers("/index/**").hasRole("visitor")
                .antMatchers("/search/**").hasRole("comsumer")
                .antMatchers("/cart/**").hasRole("manager")
                .and().formLogin().loginPage("/login").successHandler(loginSuccessHandler())
                .and().logout().logoutUrl("/logout").invalidateHttpSession(true).
                deleteCookies("JSESSIONID").logoutSuccessHandler(logoutSuccessHandler());

        // 没有全限跳转登陆页面，配置登录功能，user 和 password 要和页面 name 对应
        http.formLogin().usernameParameter("userName")
                .passwordParameter("password")
                .loginPage("/login");

        // 注销成功跳转首页
        http.logout().deleteCookies("remove").invalidateHttpSession(true).logoutSuccessUrl("/index");

        //开启记住我功能
        http.rememberMe().rememberMeParameter("remeber").rememberMeParameter("remember");

        // 关闭 csrf
        http.csrf().disable();
    }
   /**
     * 自定义认证数据源
     * passwordEncoder 密码加密
     */
    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception{
        builder.userDetailsService(userDetailService())
                .passwordEncoder(passwordEncoder());

        // 手动设置用户
        builder.inMemoryAuthentication().withUser("admin").password(new BCryptPasswordEncoder().encode("admin")).roles("admin")
        .and().withUser("root").password(new BCryptPasswordEncoder().encode("root")).roles("root");
    }
    @Bean
    public UserServiceImpl userDetailService (){
        return new UserServiceImpl () ;
    }
    /**
     * 密码加密
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() { //登出处理
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//                try {
//                    SecurityUser user = (SecurityUser) authentication.getPrincipal();
//                    logger.info("USER : " + user.getUsername() + " LOGOUT SUCCESS !  ");
//                } catch (Exception e) {
//                    logger.info("LOGOUT EXCEPTION , e : " + e.getMessage());
//                }
//                httpServletResponse.sendRedirect("/login");
            }
        };
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler loginSuccessHandler() { //登入处理
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                User userDetails = (User) authentication.getPrincipal();
//                logger.info("USER : " + userDetails.getUsername() + " LOGIN SUCCESS !  ");
//                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }
    /*
     * 硬编码几个用户
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring").password("123456").roles("LEVEL1","LEVEL2")
                .and()
                .withUser("summer").password("123456").roles("LEVEL2","LEVEL3")
                .and()
                .withUser("autumn").password("123456").roles("LEVEL1","LEVEL3");
    }
    */
}
