package com.gmall.jbasedemo.reflect.cache.controller;

import com.gmall.jbasedemo.reflect.cache.ReflectClsUtil;
import com.gmall.jbasedemo.reflect.cache.Role;
import com.gmall.jbasedemo.reflect.cache.User;
import com.gmall.jbasedemo.reflect.cache.annotation.ResourceCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-27 21:28:29
 * @description
 */
@RestController
public class ClsUserController {

    @Autowired
    private ReflectClsUtil clsUtil;

    @ResourceCheck(resourceIds = {"name"})
    @ResponseBody
    @RequestMapping("/clsUser")
    public String getUser(@ModelAttribute User user) {
        Role role = new Role();
        role.setName("admin");
        user.setRole(role);
        User reflectUser = clsUtil.getUser(user);

        return reflectUser.toString();
    }
}
