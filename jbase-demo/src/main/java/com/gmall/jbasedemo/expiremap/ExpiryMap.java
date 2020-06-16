package com.gmall.jbasedemo.expiremap;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 带有效期的Map。
 * 每个key存入该map时，都需要设置key的有效期！当超期后，该key失效！
 * 此外，该map加入了一个定时器，每间隔一段时间，就自动扫描该map一次，清除失效的key
 *
 * 设计思路：
 *  存储：过期时间集合和数据集合都以 key 为 key 分别存储在两个 map 中
 *  操作：新增：设置了过期时间在两个集合中都新增，过期时间 == -1 默认是永久有效
 *      删除：两个都删除
 *      修改：初始化时，设置是否支持更改有效时间
 *      查询：查询时增加惰性删除操作
 *  定时任务：每隔一分钟全局扫描一次；优化：随机扫描、多线程扫描
 *      清除条件：桶数组过大，大于负载值
 *          每60分钟固定清除一次
 *  线程安全：ConcurrentHashMap
 * @author:  xurui
 * @Date: 2020-06-06 23:42:51
 * @param <K>
 * @param <V>
 */
public class ExpiryMap<K, V> implements Map<K, V> {
    /**
     * 是否允许未已存在的 key 设置新的过期时间
     */
    private boolean isRefresh = false;
    private static Map workMap = new ConcurrentHashMap();
    private static Map<Object, Long> expiryMap = new ConcurrentHashMap<>();

    public ExpiryMap() {
        super();
    }

    public ExpiryMap(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    /**
     * 定时清除失效key
     * 清除条件：桶数组过大大于负载值
     *  每60分钟固定清除一次
     */
    static {
        int interval = 60;//间隔时间，单位：分
        int threshold = 10000;//map的容量阈值，达到该值后，将较频繁的执行key扫描工作！
        new Timer().schedule(new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                System.out.println("i =" + i);
                boolean isRun = ++i % interval == 0 || expiryMap.keySet().size() > threshold;
                if (isRun) {
                    removeInValidKeys();
                }
            }
        }, 60*1000, 60*1000);//每隔1分钟启动一次
    }
    private static void removeInValidKeys(){
        expiryMap.keySet().forEach(key->{
            if(expiryMap.get(key) < System.currentTimeMillis()){
                expiryMap.remove(key);
                workMap.remove(key);
            }
        });
    //    System.gc();
    }

    /**
     * put方法，需要设置key 的有效期！单位为：毫秒
     * @param key
     * @param value
     * @param expiry key的有效期，单位：毫秒
     * @return
     */
    public V put(K key, V value, long expiry) {
        //更新value，过期map中没有这个 key，或者初始化时设置支持更新失效时间为 true，才会在expiryMap 中设置过期时间
        // expiry == -1 代表这个 key 永久有效
        if (!containsKey(key) || isRefresh) {
            if(expiry > 0){
                expiryMap.put(key, System.currentTimeMillis() + expiry);
            }else{
                expiryMap.put(key, expiry);
            }
        }
        workMap.put(key, value);
        return value;
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * 是否包含 key
     * 如果已过期就删除 key
     * @param key
     * @return
     */
    @Override
    public boolean containsKey(Object key) {
        if (key != null && expiryMap.containsKey(key)) {
            long remainTime = expiryMap.get(key);
            if(remainTime > 0){
                boolean flag = remainTime > System.currentTimeMillis();
                if(!flag){
                    expiryMap.remove(key);
                    workMap.remove(key);
                }
                return flag;
            }else{
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        Collection values = workMap.values();
        if(values != null){
            return values.contains(value);
        }
        return false;
    }

    @Override
    public V get(Object key) {
        if (containsKey(key)) {
            return (V) workMap.get(key);
        }
        return null;
    }

    @Deprecated
    @Override
    public V put(K key, V value) {
        throw new RuntimeException("此方法已废弃！请加上key失效时间");
    }

    @Override
    public V remove(Object key) {
        boolean containKey = containsKey(key);
        expiryMap.remove(key);
        if(containKey){
            return (V) workMap.remove(key);
        }else{
            return null;
        }

    }

    @Deprecated
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new RuntimeException("此方法已废弃！");
    }

    @Override
    public void clear() {
        expiryMap.clear();
        workMap.clear();
    }

    @Override
    public Set<K> keySet() {
        removeInValidKeys();
        return workMap.keySet();
    }

    @Override
    public Collection<V> values() {
        removeInValidKeys();
        return workMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        removeInValidKeys();
        return workMap.entrySet();
    }
    public static void main(String[] args) {
        ExpiryMap emap = new ExpiryMap();
        emap.put("key1","value1",100000);
        emap.put("key2","value2",15);
        emap.put("key3","value3",5);
        System.out.println(emap.size());
        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(emap.size());
        System.out.println(emap.keySet());
        System.out.println(emap.values());
        System.out.println(emap.entrySet());
    }
}
