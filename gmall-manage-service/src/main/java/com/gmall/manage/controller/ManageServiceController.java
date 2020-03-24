package com.gmall.manage.controller;

import com.gmall.entity.PmsSkuInfo;
import com.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/manageService")
public class ManageServiceController {

    @Autowired
    private SkuService skuService;

    @RequestMapping("/getSkuSaleAttrValueListBySpu/{productId}")
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(@PathVariable("productId") String productId){
        return skuService.getSkuSaleAttrValueListBySpu(productId);
    }
}
