package com.gmall.zookeeperdemo.zkclient.zklock;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.concurrent.CountDownLatch;

/**
 * Zookeeper 分布式锁实现：
 * 多个线程创建同一个临时节点，如果节点创建成功就获取到锁，创建失败就注册这个节点的监听，并阻塞。
 * 当这个节点被删除时就会触发监听，不再阻塞，线程就会重新去创建这个临时节点。
 */
public class ZookeeperDistrbuteLock extends ZookeeperAbstractLock {
    private CountDownLatch countDownLatch = null;

    @Override
    //尝试获得锁
    public  boolean tryLock() {
        try {
            zkClient.createEphemeral(PATH);
            return true;
        } catch (Exception e) {
            //如果创建失败报出异常
//			e.printStackTrace();
            return false;
        }

    }

    @Override
    public void waitLock() {
        IZkDataListener izkDataListener = new IZkDataListener() {

            @Override
            public void handleDataDeleted(String path) throws Exception {
                // 唤醒被等待的线程
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
            @Override
            public void handleDataChange(String path, Object data) throws Exception {

            }
        };
        // 注册事件
        zkClient.subscribeDataChanges(PATH, izkDataListener);

        //如果节点存
        if (zkClient.exists(PATH)) {
            countDownLatch = new CountDownLatch(1);
            try {
                //等待，一直等到接受到事件通知
                countDownLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 删除监听
        zkClient.unsubscribeDataChanges(PATH, izkDataListener);
    }

    public void unLock() {
        //释放锁
        if (zkClient != null) {
            zkClient.delete(PATH);
            zkClient.close();
            System.out.println("释放锁资源...");
        }
    }
}