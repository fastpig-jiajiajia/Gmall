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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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

    /**
     * 实现思路，首先查询 redis，如果有值就直接从 redis 中查找（缓存穿透）；
     * redis 中不存在值，先去数据库查询，在这个时候，redis 中是不存在值的，如果此时大量的并发请求进来同时访问一个数据，
     * 那么请求都会直接访问数据库，导致数据库的崩溃（缓存击穿）。
     * 解决方案：在 redis 设置分布式锁（设置该键值对在一定时间内不允许被修改），
     * 第一个设置成功的 会得到返回值 OK，其余的得到 nil 值，
     * 如果得到 OK 值就查询数据库，并将值写入 redis 缓存，如果数据库中不存在该值，就将该键的值设置为空。
     * 否则的话，该线程睡眠三秒，使用自旋，保证在同一个线程进行上述操作。
     * @param skuId
     * @return
     */
    @Override
    public PmsSkuInfo getSkuById(String skuId, String str) {
        // 查找商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        ShardedJedis shardedJedis = redisUtil.getShardedJedis();
        Jedis jedis = redisUtil.getJedis();

        // 查询缓存
        String skuKey = "sku:" + skuId + ":info";
        String skuJson = shardedJedis.get(skuKey);

        if(StringUtils.isNotBlank(skuJson)){
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        }else{
            // 如果没有缓存，先查询 mysql，再存入缓存
            // 设置分布式锁，设置带有效时间的键值对，只有在键值对失效后才可以进行对该键值对的设置，设置成功返回 OK，否则返回 nil
            // 设置token，防止因访问数据库时间过长，导致redis锁失效，而其他请求设置redis分布式锁成功的情况下，
            // 自旋后误删其他请求的锁
            String token = UUID.randomUUID().toString();
            String OK = shardedJedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10*1000);
            if(StringUtils.isNotBlank(OK) && OK.equals("OK")) {
                // 设置成功有权在 10 秒的过期时间内访问数据库
                pmsSkuInfo = getSkuByFromDb(skuId);
                if(pmsSkuInfo != null) {
                    // mysql 查询结果存入 redis
                    shardedJedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                }else {
                    // 数据库中不存在该 sku
                    // 为了防止缓存穿透，将null 或者空字符串设置给 redis
                    shardedJedis.setex("sku:" + skuId + ":info", 60*3, JSON.toJSONString(""));
                }

                // 在访问mysql后，将 redis 的分布锁释放
                // 获得设置分布式锁的值，防止误删
                // 用lua脚本，在查询到key的同时删除该key，防止高并发下的意外的发生
                String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script, Collections.singletonList("lock"),Collections.singletonList(token));
                // 在高并发下，可能存在获取锁时锁未过期，但是在删除时锁已过期的可能
                String lockToken = shardedJedis.get("sku:" + skuId + ":lock");
                if(StringUtils.isNotBlank(lockToken)&&lockToken.equals(token)){
                    shardedJedis.del("sku:" + skuId + ":lock");// 用token确认删除的是自己的sku的锁
                }
            }else{
                // 设置失败，睡眠3 秒，3 秒后再次进行尝试设置值
                // 自旋（该线程在睡眠几秒后，重新尝试本地方法）
                try {
                    Thread.sleep(3000);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }

            // 必须用 return，否则会重新开辟一个线程。
            return getSkuById(skuId, "");
        }
        shardedJedis.close();
        jedis.close();
        return pmsSkuInfo;
    }


    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfoList;
    }

    @Override
    public List<PmsSkuInfo> getAllSku(String catalog3Id) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId();

            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> select = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);

            pmsSkuInfo.setSkuAttrValueList(select);
        }
        return pmsSkuInfos;
    }

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal productPrice) {

        boolean b = false;

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        BigDecimal price = pmsSkuInfo1.getPrice();

        if(price.compareTo(productPrice)==0){
            b = true;
        }


        return b;
    }


    private AtomicReference<Thread> cas = new AtomicReference<Thread>();

    public void lock() {
        Thread current = Thread.currentThread();
        // 利用CAS
        while (!cas.compareAndSet(null, current)) {
            // DO nothing
        }
    }

    public void unlock() {
        Thread current = Thread.currentThread();
        cas.compareAndSet(current, null);
    }
}
