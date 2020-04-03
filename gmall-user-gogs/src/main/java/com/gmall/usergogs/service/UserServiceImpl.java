package com.gmall.usergogs.service;

import com.gmall.usergogs.api.UserService;
import com.gmall.usergogs.api.entity.UmsMember;
import com.gmall.usergogs.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers = userMapper.selectAllUser();//userMapper.selectAllUser();
        return umsMembers;
    }
}
