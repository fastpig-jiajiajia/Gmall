package com.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gmall.entity.UmsMember;
import com.gmall.entity.UmsMemberReceiveAddress;
import com.gmall.service.UserService;
import com.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.gmall.user.mapper.UserMapper;
import com.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Override
    public List<UmsMember> getAllUser() {

        List<UmsMember> umsMembers = userMapper.selectAllUser();//userMapper.selectAllUser();

        return umsMembers;
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

    /**
     * 权限验证
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 这里可以捕获异常，使用异常映射，抛出指定的提示信息
        // 用户校验的操作
        // 假设密码是数据库查询的 123
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        UmsMember umsMember1 = loginFromDb(umsMember);

        String password = "$2a$10$XcigeMfToGQ2bqRToFtUi.sG1V.HhrJV6RBjji1yncXReSNNIPl1K";
        // 假设角色是数据库查询的
        List<String> roleList = userRoleMapper.selectByUserName(username) ;
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>() ;
        /*
         * Spring Boot 2.0 版本踩坑
         * 必须要 ROLE_ 前缀， 因为 hasRole("LEVEL1")判断时会自动加上ROLE_前缀变成 ROLE_LEVEL1 ,
         * 如果不加前缀一般就会出现403错误
         * 在给用户赋权限时,数据库存储必须是完整的权限标识ROLE_LEVEL1
         */
        if (roleList != null && roleList.size()>0){
            for (String role : roleList){
                grantedAuthorityList.add(new SimpleGrantedAuthority(role)) ;
            }
        }
        return new User(username,password,grantedAuthorityList);
    }
}
