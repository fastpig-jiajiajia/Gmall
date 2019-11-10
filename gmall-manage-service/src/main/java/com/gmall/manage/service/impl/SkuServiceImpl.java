package com.gmall.manage.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gmall.entity.PmsSkuAttrValue;
import com.gmall.entity.PmsSkuImage;
import com.gmall.entity.PmsSkuInfo;
import com.gmall.entity.PmsSkuSaleAttrValue;
import com.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.gmall.manage.mapper.PmsSkuImageMapper;
import com.gmall.manage.mapper.PmsSkuInfoMapper;
import com.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.gmall.service.SkuService;
import com.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        // 插入skuInfo
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        // 插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
    }

    /**
     * 根据skuId 查询商品商品详情
     * @param skuId
     * @return
     */
    private PmsSkuInfo getSkuByFromDb(String skuId) {
        // sku商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        // sku的图片集合
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId) {
        // 查找商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        ShardedJedis jedis = redisUtil.getJedis();

        // 查询缓存
        String skuKey = "sku:" + skuId + ":info";
        String skuJson = jedis.get(skuKey);

        if(StringUtils.isNotBlank(skuJson)){
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        }else{
            // 如果没有缓存查询 mysql，再存入缓存
            // 设置分布式锁，设置带有效时间的键值对，只有在键值对失效后才可以进行对该键值对的设置
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 10);
            if(StringUtils.isNotBlank(OK) && OK.equals("OK")) {
                // 设置成功有权在 10 秒的过期时间内访问数据库
                pmsSkuInfo = getSkuByFromDb(skuId);
                if(pmsSkuInfo != null) {
                    // mysql 查询结果存入 redis
                    jedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                }else {
                    // 数据库中不存在该 sku
                    // 为了防止缓存穿透，将null 或者空字符串设置给 redis
                    jedis.setex("sku:" + skuId + ":info", 60*3, JSON.toJSONString(""));
                }
            }else{
                // 设置失败，自旋（该线程在睡眠几秒后，重新尝试本地方法）
                try {
                    Thread.sleep(3000);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }

            // 必须用 return，否则会重新开辟一个线程。
            return getSkuById(skuId);
        }
        jedis.close();
        return pmsSkuInfo;
    }


    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfoList;
    }
}
