package com.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.entity.UmsMember;
import com.gmall.service.UserService;
import com.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/userService")
public class UserServiceController {

    @Resource(name="userServiceImpl")
    private UserService userService;

    @RequestMapping("/getAllUser")
    public List<UmsMember> getAllUser(){
        System.out.println(userService);
        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }
}
