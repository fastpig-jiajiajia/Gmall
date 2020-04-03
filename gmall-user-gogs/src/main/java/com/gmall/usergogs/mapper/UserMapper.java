package com.gmall.usergogs.mapper;


import com.gmall.usergogs.api.entity.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserMapper extends Mapper<UmsMember> {

    List<UmsMember> selectAllUser();
}
