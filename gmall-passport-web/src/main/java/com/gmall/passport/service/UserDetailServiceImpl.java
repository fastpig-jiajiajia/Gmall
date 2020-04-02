package com.gmall.passport.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.entity.SysUser;
import com.gmall.entity.UmsMember;
import com.gmall.entity.UmsRole;
import com.gmall.service.IUserDetailService;
import com.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 账户验证类，继承接口，防止成员变量无法注入问题
 */
@Service
public class UserDetailServiceImpl implements IUserDetailService {

    @Reference
    UserService userService;

    /**
     * 权限验证
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 这里可以捕获异常，使用异常映射，抛出指定的提示信息
        // 用户校验的操作
        // 假设密码是数据库查询的 123
        UmsMember umsMember = userService.getUmsMemberByUserName(username);

        String password = "$2a$10$XcigeMfToGQ2bqRToFtUi.sG1V.HhrJV6RBjji1yncXReSNNIPl1K";
        // 假设角色是数据库查询的
        List<UmsRole> roleList = new ArrayList<>();
        if(umsMember != null){
            roleList = umsMember.getUmsRoleList();
        }
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>() ;
        /*
         * Spring Boot 2.0 版本踩坑
         * 必须要 ROLE_ 前缀， 因为 hasRole("LEVEL1")判断时会自动加上ROLE_前缀变成 ROLE_LEVEL1 ,
         * 如果不加前缀一般就会出现403错误
         * 在给用户赋权限时,数据库存储必须是完整的权限标识ROLE_LEVEL1
         */
        if (CollectionUtils.isEmpty(roleList)){
            for (UmsRole role : roleList){
                grantedAuthorityList.add(new SimpleGrantedAuthority(role.getName())) ;
            }
        }

        return new SysUser(username, password);

    }


}
