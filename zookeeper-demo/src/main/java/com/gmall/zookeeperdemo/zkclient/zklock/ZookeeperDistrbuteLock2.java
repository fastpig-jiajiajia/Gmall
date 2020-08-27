package com.gmall.zookeeperdemo.zkclient.zklock;

import org.I0Itec.zkclient.IZkDataListener;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 分布式锁：
 * 首先创建一个持久节点，然后在持久节点下创建临时顺序节点，获取父节点下所有的子节点，从小到大排序，判断自己是否是第一个；
 * 如果是第一个，那么就获取到了锁，如果不是就获取自己的前一个节点，即末尾数字比自己小一的节点，然后监听这个节点，并阻塞，
 * 如果这个节点被删除了，就停止阻塞，并取消对节点的监听，重新尝试获取锁，此时就会获取到锁，
 * 当自己完成任务后需要释放掉锁，那么删除自己的临时顺序节点。
 * 为什么使用临时节点？防止宕机后发生死锁。
 * 为什么使用顺序节点？防止锁一释放所有阻塞的线程都来竞争锁的惊群现象。
 */
public class ZookeeperDistrbuteLock2 extends ZookeeperAbstractLock {
    private CountDownLatch countDownLatch = null;

    private String beforePath;//当前请求的节点前一个节点
    private String currentPath;//当前请求的节点

    public ZookeeperDistrbuteLock2() {
        if (!this.zkClient.exists(PATH2)) {
            this.zkClient.createPersistent(PATH2);
        }
    }


    @Override
    public boolean tryLock() {
        //如果currentPath为空则为第一次尝试加锁，第一次加锁赋值currentPath
        if (StringUtils.isEmpty(currentPath)) {
            //创建一个临时顺序节点
            currentPath = this.zkClient.createEphemeralSequential(PATH2 + '/', "lock");
        }
        //获取所有临时节点并排序，临时节点名称为自增长的字符串如：0000000400
        List<String> childrens = this.zkClient.getChildren(PATH2);
        Collections.sort(childrens);

        if (currentPath.equals(PATH2 + '/' + childrens.get(0))) { //如果当前节点在所有节点中排名第一则获取锁成功
            return true;
        } else { //如果当前节点在所有节点中排名中不是排名第一，则获取前面的节点名称，并赋值给beforePath
            int wz = Collections.binarySearch(childrens, currentPath.substring(7));
            beforePath = PATH2 + '/' + childrens.get(wz - 1);
        }
        return false;

    }

    @Override
    public void waitLock() {
        IZkDataListener listener = new IZkDataListener() {

            public void handleDataDeleted(String dataPath) throws Exception {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }

            public void handleDataChange(String dataPath, Object data) throws Exception {

            }
        };
        //给排在前面的的节点增加数据删除的watcher,本质是启动另外一个线程去监听前置节点
        this.zkClient.subscribeDataChanges(beforePath, listener);

        if (this.zkClient.exists(beforePath)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.zkClient.unsubscribeDataChanges(beforePath, listener);
    }


    public void unLock() {
        //删除当前临时节点
        zkClient.delete(currentPath);
        zkClient.close();
    }
}