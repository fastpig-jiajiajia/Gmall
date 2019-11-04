package com.gmall.manage.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.entity.PmsBaseCatalog1;
import com.gmall.entity.PmsBaseCatalog2;
import com.gmall.entity.PmsBaseCatalog3;
import com.gmall.manage.mapper.PmsBaseCatalog1Mapper;
import com.gmall.manage.mapper.PmsBaseCatalog2Mapper;
import com.gmall.manage.mapper.PmsBaseCatalog3Mapper;
import com.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

// Service 必须使用 Dubbo 的注解
@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    /**
     * 查询所有的一级目录
     * @return
     */
    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return pmsBaseCatalog1Mapper.selectAll();
    }

    /**
     * 根据一级目录查询二级目录
     * @param catalog1Id
     * @return
     */
    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {

        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);
        List<PmsBaseCatalog2> pmsBaseCatalog2s = pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);

        return pmsBaseCatalog2s;
    }

    /**
     * 根据二级目录查询三级目录
     * @param catalog2Id
     * @return
     */
    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {

        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2Id(catalog2Id);
        List<PmsBaseCatalog3> pmsBaseCatalog3s = pmsBaseCatalog3Mapper.select(pmsBaseCatalog3);

        return pmsBaseCatalog3s;
    }
}
