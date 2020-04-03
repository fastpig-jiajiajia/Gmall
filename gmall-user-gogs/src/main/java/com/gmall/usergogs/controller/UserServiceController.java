package com.gmall.usergogs.controller;


import com.gmall.usergogs.api.UserService;
import com.gmall.usergogs.api.entity.UmsMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/userService")
public class UserServiceController {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @RequestMapping("/getAllUser")
    public List<UmsMember> getAllUser(){
        System.out.println(userService);
        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }

}
