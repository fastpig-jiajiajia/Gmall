package com.gmall.mybatisdemo.controller;

import com.gmall.mybatisdemo.entity.User;
import com.gmall.mybatisdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserTest {

    @Autowired
    UserService userService;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void test(){

            User user = new User();
            user.setUserName("ztr");
            user.setPassWord("11111");
            user.setRealName("test");
            userService.insertUser(user);
            int a = 1/0;


    }

}
