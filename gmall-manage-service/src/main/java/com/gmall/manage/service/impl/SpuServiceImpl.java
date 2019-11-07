package com.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.entity.PmsProductImage;
import com.gmall.entity.PmsProductInfo;
import com.gmall.entity.PmsProductSaleAttr;
import com.gmall.entity.PmsProductSaleAttrValue;
import com.gmall.manage.mapper.*;
import com.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;
    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {


        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.select(pmsProductInfo);

        return pmsProductInfos;
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        // 保存商品信息
        pmsProductInfoMapper.insertSelective(pmsProductInfo);

        // 生成商品主键
        String productId = pmsProductInfo.getId();

        // 保存商品图片信息
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        for (PmsProductImage pmsProductImage : spuImageList) {
            pmsProductImage.setProductId(productId);
            pmsProductImageMapper.insertSelective(pmsProductImage);
        }

        // 保存销售属性信息
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
            pmsProductSaleAttr.setProductId(productId);
            pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);

            // 保存销售属性值
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                pmsProductSaleAttrValue.setProductId(productId);
                pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }
        }
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {

        // 先查询商品销售属性
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);

        // 查询商品属性对应商品属性值
        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrList) {
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());// 销售属性id用的是系统的字典表中id，不是销售属性表的主键
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }

        return pmsProductSaleAttrList;
    }

    @Override
    /**
     * 查找商品图片信息
     */
    public List<PmsProductImage> spuImageList(String spuId) {

        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);

        return pmsProductImages;

    }
}
