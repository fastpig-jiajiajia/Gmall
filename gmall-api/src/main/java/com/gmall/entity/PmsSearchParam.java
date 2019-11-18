package com.gmall.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索字段集合
 */
public class PmsSearchParam implements Serializable{

    // 三级目录 id
    private String catalog3Id;

    // 搜索关键字
    private String keyword;

    // 属性值数组
    private String[] valueId;

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String[] getValueId() {
        return valueId;
    }

    public void setValueId(String[] valueId) {
        this.valueId = valueId;
    }
}
