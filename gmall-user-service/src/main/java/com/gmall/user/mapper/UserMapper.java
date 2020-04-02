package com.gmall.user.mapper;

import com.gmall.entity.UmsMember;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserMapper extends Mapper<UmsMember> {

    List<UmsMember> selectAllUser();

    UmsMember selectUserOfRoles(@Param("username") String username);

}
