package com.gmall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author john
 * @date 2020/1/11 - 20:12
 */
public class SysRole implements GrantedAuthority {
    private Integer id;
    private String roleName;
    private String roleDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    //标记此属性不做json处理
    @JsonIgnore
    public String getAuthority() {
        return roleName;
    }
}
