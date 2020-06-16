package com.gmall.mongodbdemo.controller;

import com.gmall.mongodbdemo.entity.User;
import com.gmall.mongodbdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MongoDBController {

    @Autowired
    private UserService userService;


    /**
     * 重复的值不会插入，默认字段名为 id 的就是主键，如果没有就设置默认主键
     * @return
     */
    @RequestMapping("insertUserList2Mongo")
    public String insertUserList2Mongo(){
        List<User> userList = userService.selectAllUser();
        userService.insertUser2Mongo(userList);

        return "success";
    }


    /**
     * 重复的值不会插入，
     * @return
     */
    @RequestMapping("insertUser2Mongo")
    public String insertUser2Mongo(){
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setId(1000);
        user.setUserName("多少的委屈");
        user.setPassWord("1111");
        user.setRealName("我去饿我去恶趣味");
        userList.add(user);

        userService.insertUser2Mongo(userList);

        return "success";
    }


    /**
     * 查询所有 大于 当前 id 的用户信息列表
     * @param id
     * @return
     */
    @RequestMapping("selectAllUserFromMongo/{id}")
    public List<User> selectAllUserFromMongo(@PathVariable("id") int id) {
        return userService.selectAllUserFromMongoDB(id);
    }

    /**
     * 查询当前 id 的用户信息
     * @param id
     * @return
     */
    @RequestMapping("selectUserOneFromMongo/{id}")
    public User selectUserOneFromMongo(@PathVariable("id") int id) {
        User user = new User();
        user.setId(id);

        return userService.selectUserFromMongoDB(user);
    }


    /**
     * 根据 id 更新当前用户信息
     */
    @RequestMapping("updateUserFromMongo/{id}")
    public long updateUserFromMongo(@PathVariable("id") int id) {
        User user = userService.selectUserOne(id);
        user.setUserName("11111111111111111");

        return userService.updateUser2MongoDB(user);
    }


    /**
     * 根据 id 删除当前用户信息
     */
    @RequestMapping("deleteUserFromMongo/{id}")
    public long deleteUserFromMongo(@PathVariable("id") int id) {
        User user = userService.selectUserOne(id);

        return userService.deleteUserFromMongoDB(user);
    }
}
