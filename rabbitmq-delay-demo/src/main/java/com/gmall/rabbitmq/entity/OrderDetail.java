package com.gmall.rabbitmq.entity;

import java.util.List;

public class OrderDetail {
    private String id;
    private String orderId;
    private String itemId;
    private String detail;
    private List<Item> itemList;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return "OrderDetail{" + "id='" + id + '\'' + ", orderId='" + orderId + '\'' + ", itemId='" + itemId + '\'' + ", detail='" + detail + '\'' + ", itemList=" + itemList + '}';
    }
}
