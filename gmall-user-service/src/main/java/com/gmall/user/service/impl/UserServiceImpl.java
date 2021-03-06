package com.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gmall.entity.UmsMember;
import com.gmall.entity.UmsMemberReceiveAddress;
import com.gmall.service.UserService;
import com.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.gmall.user.mapper.UserMapper;
import com.gmall.util.RedisUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Service
// @CacheConfig(cacheNames = {"users"})
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })


    /**
     * // 缓存到本地
     * @Cacheable可以指定三个属性，value、key和condition。
    value属性指定cache的名称（即选择ehcache.xml中哪种缓存方式存储）
    key属性是用来指定Spring缓存方法的返回结果时对应的key的。该属性支持SpringEL表达式。当我们没有指定该属性时，Spring将使用默认策略生成key。
     * @return
     */
    @Override
//    @CachePut(value = "userCache")
    public List<UmsMember> getAllUser() {

        List<UmsMember> umsMembers = userMapper.selectAllUser();//userMapper.selectAllUser();

        int i = 1 / 0;

        return umsMembers;
    }

    /**
     * 清空 #UmsMemberList 缓存
     */
    @Override
//    @CacheEvict(value="userCache")   //allEntries=true
    public void clearEhCache(){
        return ;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {

        // 封装的参数对象
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);


//       Example example = new Example(UmsMemberReceiveAddress.class);
//       example.createCriteria().andEqualTo("memberId",memberId);
//       List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);

        return umsMemberReceiveAddresses;
    }

    /**
     * 登陆验证
     * @param umsMember
     * @return
     */
    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();

            if(jedis!=null){
                String umsMemberStr = jedis.get("user:" + umsMember.getPassword() + ":info");

                if (StringUtils.isNotBlank(umsMemberStr)) {
                    // 密码正确
                    UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
                    return umsMemberFromCache;
                }
            }
            // 链接redis失败，开启数据库
            UmsMember umsMemberFromDb = loginFromDb(umsMember);
            // 验证成功redis 保留一份
            if(umsMemberFromDb != null){
                jedis.setex("user:" + umsMember.getPassword() + ":info", 60*60*24, JSON.toJSONString(umsMemberFromDb));
            }
            return umsMemberFromDb;
        }finally {
            jedis.close();
        }
    }

    @Override
    public UmsMember checkOauthUser(UmsMember umsCheck) {
        UmsMember umsMember = userMapper.selectOne(umsCheck);
        return umsMember;
    }

    @Override
    public void addOauthUser(UmsMember umsMember) {
        userMapper.insertSelective(umsMember);
    }


    @Override
    public UmsMember getOauthUser(UmsMember umsMemberCheck) {


        UmsMember umsMember = userMapper.selectOne(umsMemberCheck);
        return umsMember;
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(receiveAddressId);
        UmsMemberReceiveAddress umsMemberReceiveAddress1 = umsMemberReceiveAddressMapper.selectOne(umsMemberReceiveAddress);
        return umsMemberReceiveAddress1;
    }

    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis = redisUtil.getJedis();

        jedis.setex("user:"+memberId+":token",60*60*2,token);

        jedis.close();
    }

    /**
     * 从数据库验证用户数据
     * @param umsMember
     * @return
     */
    private UmsMember loginFromDb(UmsMember umsMember) {
        List<UmsMember> umsMembers  = new ArrayList<>();
        umsMembers = userMapper.select(umsMember);

        if(!umsMembers.isEmpty()){
            return umsMembers.get(0);
        }

        return null;
    }

    @Override
    public UmsMember getUmsMemberByUserName(String userName){
        return userMapper.selectUserOfRoles(userName);
    }
}
