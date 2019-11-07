package com.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.entity.PmsSkuInfo;
import com.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class SkuController {

    @Reference
    private SkuService skuService;

    @RequestMapping("saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){

        // 将spuId封装给productId
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());

        // 处理默认图片
        String skuDefaultImg = pmsSkuInfo.getSkuDefaultImg();
        if(StringUtils.isBlank(skuDefaultImg)){
            pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
        }

        skuService.saveSkuInfo(pmsSkuInfo);

        return "success";
    }
}
