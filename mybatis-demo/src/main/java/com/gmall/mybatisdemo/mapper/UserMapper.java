package com.gmall.mybatisdemo.mapper;

import com.gmall.mybatisdemo.entity.Order;
import com.gmall.mybatisdemo.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    public User getUserById(@Param("id") String id, @Param("username") String username, @Param("realname") String realname, @Param("passwordList") List<String> passwordList);

    public Order getUserOrderById(String id);

    public Order getOrderDetailById(String id);

    public Order getOrderAllById(String id);

    public Order getUserOrderByIdLazyLoading(String id);

    @Select("select * from user")
    public List<User> getAllUser();

    @Update("update user set userName = #{userName} where id = #{id}")
    public int updateUserNameById(@Param("id") String id, @Param("userName") String userName );

    public void insertBatchUser(List<User> userList);


    public List<User> selectByMap(Map<String, Object> map);

    @Insert("insert into user(username, password, realname) values(#{user.userName}, #{user.passWord}, #{user.realName})")
    void insertUser(@Param("user") User user);
}
