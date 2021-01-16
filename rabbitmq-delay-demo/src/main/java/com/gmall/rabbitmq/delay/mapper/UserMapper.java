package com.gmall.rabbitmq.delay.mapper;


import com.gmall.rabbitmq.delay.entity.Order;
import com.gmall.rabbitmq.delay.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    User getUserById(@Param("id") String id, @Param("username") String username, @Param("realname") String realname, @Param("passwordList") List<String> passwordList);

    Order getUserOrderById(String id);

    Order getOrderDetailById(String id);

    Order getOrderAllById(String id);

    Order getUserOrderByIdLazyLoading(String id);

    @Select("select * from user")
    List<User> getAllUser();

    @Update("update user set userName = #{userName} where id = #{id}")
    int updateUserNameById(@Param("id") String id, @Param("userName") String userName );

    void insertBatchUser(List<User> userList);


    List<User> selectByMap(Map<String, Object> map);

    @Insert("insert into user(username, password, realname) values(#{user.userName}, #{user.passWord}, #{user.realName})")
    void insertUser(@Param("user") User user);
}
