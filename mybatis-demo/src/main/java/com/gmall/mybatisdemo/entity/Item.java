package com.gmall.mybatisdemo.entity;

public class Item {
    private String id;
    private String itemName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return "Item{" + "id='" + id + '\'' + ", itemName='" + itemName + '\'' + '}';
    }
}
