package com.gmall.service;

import com.gmall.entity.PmsSearchParam;
import com.gmall.entity.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
