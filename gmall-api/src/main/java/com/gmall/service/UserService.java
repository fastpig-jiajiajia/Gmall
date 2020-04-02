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

    void addUserToken(String token, String memberId);

    UmsMember login(UmsMember umsMember);

    UmsMember checkOauthUser(UmsMember umsCheck);

    void addOauthUser(UmsMember umsMember);

    UmsMember getOauthUser(UmsMember umsMemberCheck);

    UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId);

    public UmsMember getUmsMemberByUserName(String userName);


    public void clearEhCache();
}
