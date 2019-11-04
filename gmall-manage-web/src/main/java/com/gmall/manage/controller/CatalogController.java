package com.gmall.manage.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.entity.PmsBaseCatalog1;
import com.gmall.entity.PmsBaseCatalog2;
import com.gmall.entity.PmsBaseCatalog3;
import com.gmall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin  // 跨域解决注解
public class CatalogController {

    // dubbo 的 Refrence 注解
    @Reference
    CatalogService catalogService;

    /**
     * 得到一级目录
     * @return
     */
    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1(){

        List<PmsBaseCatalog1> catalog1s = catalogService.getCatalog1();
        return catalog1s;
    }

    /**
     * 根据一级目录得到二级目录
     * @param catalog1Id
     * @return
     */
    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id){

        List<PmsBaseCatalog2> catalog2s = catalogService.getCatalog2(catalog1Id);
        return catalog2s;
    }

    /**
     * 根据二级目录得到三级目录
     * @param catalog2Id
     * @return
     */
    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id){

        List<PmsBaseCatalog3> catalog3s = catalogService.getCatalog3(catalog2Id);
        return catalog3s;
    }
}
