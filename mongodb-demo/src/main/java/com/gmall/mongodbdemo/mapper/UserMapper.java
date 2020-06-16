package com.gmall.mongodbdemo.mapper;

import com.gmall.mongodbdemo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user")
    public List<User> selectAllUser();

    @Select("select * from user where id=#{id}")
    public User selectUserOne(int id);
}
