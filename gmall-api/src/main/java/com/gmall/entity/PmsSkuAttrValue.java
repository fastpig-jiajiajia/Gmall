package com.gmall.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Sku 关联表
 * attrId : pms_base_attr_info 商品平台属性表的主键
 * valueId : pms_base_attr_value 商品平台属性值表的主键
 * skuId : pms_sku_info Sku 信息表的主键
 */
public class PmsSkuAttrValue implements Serializable {

    @Id
    @Column
    String id;

    @Column
    String attrId;

    @Column
    String valueId;

    @Column
    String skuId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }
}
