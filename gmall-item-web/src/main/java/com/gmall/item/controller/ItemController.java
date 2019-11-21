package com.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gmall.entity.PmsProductSaleAttr;
import com.gmall.entity.PmsSkuImage;
import com.gmall.entity.PmsSkuInfo;
import com.gmall.entity.PmsSkuSaleAttrValue;
import com.gmall.service.SkuService;
import com.gmall.service.SpuService;
import org.apache.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;

    /**
     * thymeleaf 测试
     * @param modelMap
     * @return
     */
    @RequestMapping("index")
    public String index(ModelMap modelMap){

        List<String> list = new ArrayList<>();
        for (int i = 0; i <5 ; i++) {
            list.add("循环数据"+i);
        }

        modelMap.put("list",list);
        modelMap.put("hello","hello thymeleaf !!");

        modelMap.put("check","0");


        return "index";
    }

    @RequestMapping("nice")
    public String nice(){
        System.out.println("what");
        return "nice";
    }

    /**
     * 商品详情
     * @param skuId
     * @param modelMap
     * @return
     */
    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap modelMap, HttpServletRequest request){
        // 得到请求的 ip
        String remoteAttr = request.getRemoteAddr();
        // 如果是通过 Nginx 进行反向代理，得到是 Nginx 的 ip
        request.getHeader("");

        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId, "");
        modelMap.put("skuInfo", pmsSkuInfo);

        //销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());
        modelMap.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);

        // 查询当前sku所属的商品的其他的sku列表，放到页面缓存，减少查询
        Map<String, String> skuSaleAttrMap = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfoList = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

        for(PmsSkuInfo pmsSkuInfo1 : pmsSkuInfoList){
            String key = "";
            String value = pmsSkuInfo1.getId();
            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = pmsSkuInfo1.getSkuSaleAttrValueList();
            for(PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValueList){
                key += pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";
            }

            skuSaleAttrMap.put(key, value);
        }
        // 将该商品的sku列表放到页面
        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrMap);
        modelMap.put("skuSaleAttrHashJsonStr", skuSaleAttrHashJsonStr);

        return "item";
    }

}
