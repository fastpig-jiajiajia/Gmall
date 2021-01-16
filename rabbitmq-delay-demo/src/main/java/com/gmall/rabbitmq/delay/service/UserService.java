package com.gmall.rabbitmq.delay.service;

import com.gmall.rabbitmq.delay.entity.Order;
import com.gmall.rabbitmq.delay.entity.User;
import com.gmall.rabbitmq.delay.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getUserById(String id, String username, String realname, List<String> passwordList){
        return userMapper.getUserById(id, username, realname, passwordList);
    }

    /**
     * 获取 用户的单个订单一对一关系
     * @param id
     * @return
     */
    public Order getUserOrderById(String id){
        return userMapper.getUserOrderById(id);
    }


    /**
     * 获取用户订单和订单详情，1对多
     */
    public Order getOrderDetailById(String id){
        return userMapper.getOrderDetailById(id);
    }


    /**
     * 获取一个订单所有信息，订单详情：商品；多对多
     * @param id
     * @return
     */
    public Order getOrderAllById(String id){
        return userMapper.getOrderAllById(id);
    }


    /**
     * 延迟加载
     * @param id
     * @return
     */
    public Order getUserOrderByIdLazyLoading(String id) {
        return userMapper.getUserOrderByIdLazyLoading(id);
    }

    /**
     * 查询所有的用户
     */
    public List<User> getAllUser(){
        return userMapper.getAllUser();
    }

    /**
     * 更新用户
     */
    public int updateUserNameById(String id, String userName ){
        return userMapper.updateUserNameById(id, userName);
    }

    /**
     * 批量添加
     */
    public void insertBatchUser(List<User> userList){
        userMapper.insertBatchUser(userList);
    }

    /**
     * map 传参
     * @param map
     * @return
     */
    public List<User> selectByMap(Map<String, Object> map){
        return userMapper.selectByMap(map);
    }

    public void insertUser(User user) {
        userMapper.insertUser(user);
    }
}
