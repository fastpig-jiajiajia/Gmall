package com.gmall.zookeeperdemo.zkclient.zklock;

/**
 * Created by VULCAN on 2018/9/20.
 */
public interface Lock {
    //获取到锁的资源
    public void getLock();
    // 释放锁
    public void unLock();
}
