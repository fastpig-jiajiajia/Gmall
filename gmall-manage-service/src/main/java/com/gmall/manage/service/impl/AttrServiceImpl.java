package com.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.entity.PmsBaseAttrInfo;
import com.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.gmall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 *  商品属性 Service 实现类
 */
@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    /**
     * 根据三级目录得到所有的属性
     * @param catalog3Id
     * @return
     */
    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {

        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
        return pmsBaseAttrInfos;
    }
}
