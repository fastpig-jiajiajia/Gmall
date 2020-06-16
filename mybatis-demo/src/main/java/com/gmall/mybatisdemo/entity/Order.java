package com.gmall.mybatisdemo.entity;

import java.util.List;

public class Order {

    private String id;
    private String userId;
    private String orderName;
    private User user;
    private List<OrderDetail> orderDetailList;
    private List<Item> itemList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return "Order{" + "id='" + id + '\'' + ", userId='" + userId + '\'' + ", orderName='" + orderName + '\'' + ", user=" + user + ", orderDetailList=" + orderDetailList + ", itemList=" + itemList + '}';
    }
}
