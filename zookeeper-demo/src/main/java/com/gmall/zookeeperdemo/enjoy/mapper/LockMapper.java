package com.gmall.zookeeperdemo.enjoy.mapper;

/**
 * Created by VULCAN on 2018/9/29.
 */
public interface LockMapper {

    //删除数据解锁
    int deleteByPrimaryKey(int id);

    //新增数据加锁，id为同一个值
    int insert(int id);

}