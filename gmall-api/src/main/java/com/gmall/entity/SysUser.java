package com.gmall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author john
 * @date 2020/1/11 - 20:15
 */
public class SysUser implements UserDetails {
    private Integer id;
    private String username;
    private String password;
    private Integer status;
    private List<SysRole> roles = new ArrayList<SysRole>();

    public SysUser() {
    }

    public SysUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public SysUser(String username, String password, List<SysRole> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public SysUser(Integer id, String username, String password, Integer status, List<SysRole> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
        this.roles = roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
