package com.gmall.usergogs.api;

import com.gmall.usergogs.api.entity.UmsMember;

import java.util.List;

/**
 * 用户 Service
 */
public interface UserService {

    List<UmsMember> getAllUser();
}
