package com.gmall.mybatisdemo.controller;

import com.gmall.mybatisdemo.entity.User;
import com.gmall.mybatisdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class UserTest {

    @Autowired
    UserService userService;


    @Transactional
    public void test() throws IOException{
        User user = new User();
        user.setUserName("ztr");
        user.setPassWord("11111");
        user.setRealName("test");
        userService.insertUser(user);

        throw new IOException("11");
    }

}
