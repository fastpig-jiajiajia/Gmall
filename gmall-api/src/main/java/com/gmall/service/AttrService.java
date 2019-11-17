package com.gmall.service;

import com.gmall.entity.PmsBaseAttrInfo;
import com.gmall.entity.PmsBaseAttrValue;
import com.gmall.entity.PmsBaseSaleAttr;

import java.util.List;
import java.util.Set;

/**
 * 商品属性 Service
 */
public interface AttrService {
    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();

    List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet);
}
