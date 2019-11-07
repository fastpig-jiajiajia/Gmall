package com.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.entity.PmsBaseAttrInfo;
import com.gmall.entity.PmsBaseAttrValue;
import com.gmall.entity.PmsBaseSaleAttr;
import com.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品属性 Service 实现类
 */
@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;
    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    /**
     * 根据三级目录得到所有的属性
     *
     * @param catalog3Id
     * @return
     */
    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {

        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);

        for (PmsBaseAttrInfo baseAttrInfo : pmsBaseAttrInfos) {

            List<PmsBaseAttrValue> pmsBaseAttrValues = new ArrayList<>();
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(baseAttrInfo.getId());
            pmsBaseAttrValues = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            baseAttrInfo.setAttrValueList(pmsBaseAttrValues);
        }

        return pmsBaseAttrInfos;
    }

    /**
     * 插入及修改商品属性及属性值
     *
     * @param pmsBaseAttrInfo
     * @return
     */
    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        String success = "success";
        List<PmsBaseAttrValue> pmsBaseAttrValuesList = pmsBaseAttrInfo.getAttrValueList();
        try {
            // 根据 id 是否为空判断是插入还是修改
            if (StringUtils.isBlank(pmsBaseAttrInfo.getId())) {
                // insert 空值插入 insertSelective 空值不插
                pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);

                for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrValuesList) {
                    pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                    pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
                }
            } else {
                // 属性修改
                Example example = new Example(pmsBaseAttrInfo.getClass());
                example.createCriteria().andEqualTo("id", pmsBaseAttrInfo.getId());
                pmsBaseAttrInfoMapper.updateByExample(pmsBaseAttrInfo, example);

                // 属性值修改
                for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrValuesList) {
                    Example example1 = new Example(pmsBaseAttrValue.getClass());
                    example1.createCriteria().andEqualTo("id", pmsBaseAttrValue.getId());
                    pmsBaseAttrValueMapper.updateByExample(pmsBaseAttrValue, example1);
                }
            }

        } catch (Exception e) {
            success = "failed";
            e.printStackTrace();

        } finally {
            return success;
        }
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {

        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValueList = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
        return pmsBaseAttrValueList;
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return pmsBaseSaleAttrMapper.selectAll();
    }
}
