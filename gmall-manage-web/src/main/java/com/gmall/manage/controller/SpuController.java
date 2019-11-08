package com.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.entity.PmsProductImage;
import com.gmall.entity.PmsProductInfo;
import com.gmall.entity.PmsProductSaleAttr;
import com.gmall.manage.util.PmsUploadUtil;
import com.gmall.service.SpuService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;

    /**
     * 根据三级目录获取商品基本信息
     *
     * @param catalog3Id
     * @return
     */
    @RequestMapping("spuList")
    public List<PmsProductInfo> spuList(String catalog3Id) {

        List<PmsProductInfo> pmsProductInfos = spuService.spuList(catalog3Id);

        return pmsProductInfos;
    }

    /**
     * 保存属性
     *
     * @param pmsProductInfo
     * @return
     */
    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {

        spuService.saveSpuInfo(pmsProductInfo);

        return "success";
    }

    /**
     * 上传图片到分布式系统
     * @param multipartFile
     * @return
     */
    @RequestMapping("fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {
        // 将图片或者音视频上传到分布式的文件存储系统

        // 将图片的存储路径返回给页面
        String imgUrl = PmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);

        return imgUrl;
    }
<<<<<<< HEAD

    /**
     * 根据商品 id 查询商品 属性对应值信息
     * @param spuId
     * @return
     */
    @RequestMapping("spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){

        List<PmsProductSaleAttr> pmsProductSaleAttrList = spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrList;
    }

    /**
     * 根据商品 id 查询商品图片信息列表
     * @param spuId
     * @return
     */
    @RequestMapping("spuImageList")
    public List<PmsProductImage> spuImageList(String spuId){

        List<PmsProductImage> pmsProductImages = spuService.spuImageList(spuId);
        return pmsProductImages;
    }

    public void test(){
        
    }
=======
>>>>>>> parent of dbe31e3... feature2 第1次提交
}
