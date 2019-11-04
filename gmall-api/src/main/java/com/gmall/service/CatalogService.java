package com.gmall.service;

import com.gmall.entity.PmsBaseCatalog1;
import com.gmall.entity.PmsBaseCatalog2;
import com.gmall.entity.PmsBaseCatalog3;

import java.util.List;

/**
 * 目录 Service
 */
public interface CatalogService {
    List<PmsBaseCatalog1> getCatalog1();

    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);

    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
