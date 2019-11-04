package com.gmall.service;

import com.gmall.entity.PmsBaseAttrInfo;

import java.util.List;

/**
 * 商品属性 Service
 */
public interface AttrService {
    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);
}
