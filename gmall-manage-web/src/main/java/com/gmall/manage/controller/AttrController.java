package com.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.entity.PmsBaseAttrInfo;
import com.gmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController  {

    @Reference
    AttrService attrService;

    /**
     * 保存商品属性
     * @param pmsBaseAttrInfo
     * @return
     */
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){

        return "success";
    }

    /**
     * 根据三级目录得到商品属性列表
     * @param catalog3Id
     * @return
     */
    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){

        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.attrInfoList(catalog3Id);
        return pmsBaseAttrInfos;
    }
}
