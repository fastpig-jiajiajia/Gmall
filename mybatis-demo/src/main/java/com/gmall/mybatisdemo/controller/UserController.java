package com.gmall.mybatisdemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.gmall.mybatisdemo.entity.User;
import com.gmall.mybatisdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private UserTest userTest;

    @RequestMapping("/getUserById/{id}")
    public String getUserById(@PathVariable("id") String id){

        String username = "x%";
        String realname = "徐锐";
        List<String> passwordList = new ArrayList<String>(){
            {
                add("11111");
                add("22222");
            }
        };
        return JSONObject.toJSONString(userService.getUserById(id, username, realname, passwordList));
    }

    /**
     * 一对一
     * @param id
     * @return
     */
    @RequestMapping("getUserOrderById/{id}")
    public String getUserOrderById(@PathVariable("id") String id){

        return JSONObject.toJSONString(userService.getUserOrderById(id));
    }

    /**
     * 一对多
     * @param id
     * @return
     */
    @RequestMapping("getOrderDetailById/{id}")
    public String getOrderDetailById(@PathVariable("id") String id){

        return JSONObject.toJSONString(userService.getOrderDetailById(id));
    }

    /**
     * 多对多
     * @param id
     * @return
     */
    @RequestMapping("getOrderAllById/{id}")
    public String getOrderAllById(@PathVariable("id") String id){

        return JSONObject.toJSONString(userService.getOrderAllById(id));
    }

    /**
     * 延迟加载
     * @param id
     * @return
     */
    @RequestMapping("getUserOrderByIdLazyLoading/{id}")
    public String getUserOrderByIdLazyLoading(@PathVariable("id") String id){

        return JSONObject.toJSONString(userService.getUserOrderByIdLazyLoading(id));
    }


    /**
     * 获取所有的用户
     */
    @RequestMapping("/getAllUser")
    public String getAllUser(){
        return JSONObject.toJSONString(userService.getAllUser());
    }

    /**
     * 更新用户
     */
    @RequestMapping("/updateUserNameById/{id}/{userName}")
    public String updateUserNameById(@PathVariable("id") String id, @PathVariable("userName")String userName ){
        return String.valueOf(userService.updateUserNameById(id, userName));
    }

    /**
     * 批处理插入数据
     */
    @RequestMapping("/insertBatchUser")
    public String insertBatchUser(){
        List<User> userList = new ArrayList<>();

        User user = new User();
    //    user.setId(100);
        user.setUserName("ztr");
        user.setPassWord("22222");
        user.setRealName("朱涛然");
        userList.add(user);

        // 必须重新创建，否则插入的是同样的对象，上一个 add 是对 user 的引用
        User user1 = new User();
    //    user1.setId(101);
        user1.setUserName("sjm");
        user1.setPassWord("33333");
        user1.setRealName("邵加明");
        userList.add(user1);

        userService.insertBatchUser(userList);
        return "success";
    }


    /**
     * 批处理更新
     */


    /**
     * map 传参
     */
    @RequestMapping("/selectByMap")
    public List<User> selectByMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("password", "11111");
        map.put("realName", "邵加明");

        return userService.selectByMap(map);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping("/insertUser")
    public String insertUser(){
        try{
            User user = new User();
            //    user.setId(100);
            user.setUserName("ztr");
            user.setPassWord("22222");
            user.setRealName("userTest");
            userService.insertUser(user);

            userTest.test();
        }catch (Exception e){
            e.printStackTrace();
        }

        return "success";
    }


    @Transactional
    public void method(){

    }

}
