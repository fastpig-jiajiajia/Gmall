package com.gmall.zookeeperdemo.zkclient;

import org.I0Itec.zkclient.*;
import org.apache.zookeeper.Watcher;
import org.junit.Test;

import java.util.List;

/**
 * ZkClient Watcher 机制
 */
public class TestZkClientWatcher {


    /** zookeeper地址 */
    static final String CONNECT_ADDR = "39.101.198.56:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 10000;//ms


    @Test
    /**
     * subscribeChildChanges方法 订阅子节点变化
     */
    public  void testZkClientWatcher1() throws Exception {
        ZkClient zkc = new ZkClient(new ZkConnection(CONNECT_ADDR), SESSION_OUTTIME);

        // 对父节点添加监听子节点变化，父节点的变化也会触发
        // 只在创建、删除节点时触发，读取更新不触发
        zkc.subscribeChildChanges("/super", new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("parentPath: " + parentPath);
                System.out.println("currentChilds: " + currentChilds);
            }
        });

        zkc.createPersistent("/super");  //触发 handleChildChange
        Thread.sleep(1000);
        zkc.writeData("/super", "1111111"); // 不触发
        Thread.sleep(1000);

        zkc.createPersistent("/super" + "/" + "c1", "c1内容"); //触发 handleChildChange
        Thread.sleep(1000);
        zkc.writeData("/super" + "/" + "c1", "c1 内容 new", -1); // 不触发
        Thread.sleep(1000);

        zkc.createPersistent("/super" + "/" + "c2", "c2内容"); //触发 handleChildChange
        Thread.sleep(1000);

        zkc.delete("/super/c2"); //触发 handleChildChange
        Thread.sleep(1000);

        zkc.deleteRecursive("/super"); //触发 handleChildChange
        System.out.println();
    }

    @Test
    /**
     * subscribeDataChanges 订阅内容变化
     */
    public void testZkClientWatcher2() throws Exception {
        ZkClient zkc = new ZkClient(new ZkConnection(CONNECT_ADDR), SESSION_OUTTIME);

//        zkc.createPersistent("/super", "123");  // 触发 handleDataChange

        // 对父节点添加监听子节点变化。
        // 监听该节点的创建、修改、删除，但是其子节点所有的操作都不监听
        zkc.subscribeDataChanges("/super", new IZkDataListener() {
            @Override
            public void handleDataDeleted(String path) throws Exception {
                System.out.println("删除的节点为:" + path);
            }

            @Override
            public void handleDataChange(String path, Object data) throws Exception {
                System.out.println("变更的节点为:" + path + ", 变更内容为:" + data);
            }
        });

        zkc.createPersistent("/super", "123");  // 触发 handleDataChange
        Thread.sleep(1000);
        zkc.createPersistent("/super/c1", "c1"); // 不触发
        Thread.sleep(1000);
        zkc.createPersistent("/super/c2", "c2"); // 不触发
        Thread.sleep(1000);


        zkc.writeData("/super", "456", -1);  // 触发 handleDataChange
        Thread.sleep(1000);

        zkc.writeData("/super/c1", "c1 new", -1); // 不触发
        Thread.sleep(1000);

        zkc.writeData("/super/c2", "c2 new", -1); // 不触发
        Thread.sleep(1000);

        zkc.delete("/super/c1");
        Thread.sleep(1000);
        zkc.delete("/super/c2");
        Thread.sleep(1000);
        zkc.delete("/super"); // 触发 handleDataDeleted
        Thread.sleep(1000);
        System.out.println();
    }

    @Test
    public void testZkClientWatcher3() throws Exception {
        ZkClient zkc = new ZkClient(new ZkConnection(CONNECT_ADDR), SESSION_OUTTIME);

//        zkc.createPersistent("/super", "123");  // 触发 handleDataChange

        // 订阅/取消订阅连接状态变化事件
        zkc.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {

            }

            @Override
            public void handleNewSession() throws Exception {

            }

            @Override
            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {

            }
        });

        zkc.createPersistent("/super", "123");  // 触发 handleDataChange
        Thread.sleep(1000);
        zkc.createPersistent("/super/c1", "c1"); // 不触发
        Thread.sleep(1000);
        zkc.createPersistent("/super/c2", "c2"); // 不触发
        Thread.sleep(1000);


        zkc.writeData("/super", "456", -1);  // 触发 handleDataChange
        Thread.sleep(1000);

        zkc.writeData("/super/c1", "c1 new", -1); // 不触发
        Thread.sleep(1000);

        zkc.writeData("/super/c2", "c2 new", -1); // 不触发
        Thread.sleep(1000);

        zkc.delete("/super/c1");
        Thread.sleep(1000);
        zkc.delete("/super/c2");
        Thread.sleep(1000);
        zkc.delete("/super"); // 触发 handleDataDeleted
        Thread.sleep(1000);
        System.out.println();
    }


    public static void main(String[] args) {
        TestZkClientWatcher testZkClientWatcher = new TestZkClientWatcher();
        try {
//            testZkClientWatcher.testZkClientWatcher1();
            testZkClientWatcher.testZkClientWatcher2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
