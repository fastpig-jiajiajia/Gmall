package com.gmall.service;

import com.gmall.entity.UmsMember;
import com.gmall.entity.UmsMemberReceiveAddress;

import java.util.List;

/**
 * 用户 Service
 */
public interface UserService {

    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
