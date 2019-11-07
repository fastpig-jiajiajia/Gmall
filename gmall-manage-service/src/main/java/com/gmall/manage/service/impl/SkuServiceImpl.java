package com.gmall.manage.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.entity.PmsSkuAttrValue;
import com.gmall.entity.PmsSkuImage;
import com.gmall.entity.PmsSkuInfo;
import com.gmall.entity.PmsSkuSaleAttrValue;
import com.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.gmall.manage.mapper.PmsSkuImageMapper;
import com.gmall.manage.mapper.PmsSkuInfoMapper;
import com.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        // 插入skuInfo
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        // 插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
    }
}
