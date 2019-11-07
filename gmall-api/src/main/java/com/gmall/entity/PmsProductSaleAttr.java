package com.gmall.entity;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * 商家销售属性
 * 商品属性：颜色，尺寸...
 * 通过 productid 关联，一个商品对应多个商品属性
 * 一个商品销售属性对应多个商品销售属性值
 * attrid 相当于平台商品属性，通过 attr_id 关联商品属性值
 */
public class PmsProductSaleAttr implements Serializable {

    @Id
    @Column
    String id ;

    @Column
    String productId;

    @Column
    String saleAttrId;

    @Column
    String saleAttrName;


    @Transient
    List<PmsProductSaleAttrValue> spuSaleAttrValueList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSaleAttrId() {
        return saleAttrId;
    }

    public void setSaleAttrId(String saleAttrId) {
        this.saleAttrId = saleAttrId;
    }

    public String getSaleAttrName() {
        return saleAttrName;
    }

    public void setSaleAttrName(String saleAttrName) {
        this.saleAttrName = saleAttrName;
    }

    public List<PmsProductSaleAttrValue> getSpuSaleAttrValueList() {
        return spuSaleAttrValueList;
    }

    public void setSpuSaleAttrValueList(List<PmsProductSaleAttrValue> spuSaleAttrValueList) {
        this.spuSaleAttrValueList = spuSaleAttrValueList;
    }
}
