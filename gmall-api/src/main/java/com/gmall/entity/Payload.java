package com.gmall.entity;


import java.util.Date;

/**
 * @author john
 * @date 2020/1/12 - 9:15
 */
public class Payload<T> {
    private String id;
    private T userInfo;
    private Date expiration;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(T userInfo) {
        this.userInfo = userInfo;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}