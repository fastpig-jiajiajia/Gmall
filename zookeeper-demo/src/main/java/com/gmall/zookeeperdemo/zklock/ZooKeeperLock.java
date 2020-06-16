package com.gmall.zookeeperdemo.zklock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * zookeeper 实现分布式锁
 */
public class ZooKeeperLock {

    // 设置连接超时时间
    private static final int SESSION_TIMEOUT = 5000;
    // zookeeper 集群地址
    private String hosts =
            "192.168.25.141:2181,192.168.25.142:2181,192.168.25.143:2181";
    private String groupNode = "locks";
    private String subNode = "sub";
    private boolean haveLock = false;

    private ZooKeeper zk;
    // 记录自己创建的子节点路径
    private  String thisPath;


    /**
     * 判断 group 路径是否存在， 若不存在则创建
     * @throws Exception
     */
    public void createGroupNode() throws Exception {
        Stat exists = zk.exists("/" + groupNode, false);
        if(exists == null) {
            zk.create("/" + groupNode, "parent Node".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    /**
     * 连接zookeeper
     * @throws Exception
     */
    public void connectZookeeper() throws Exception {
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, new Watcher() {

            @Override
            public void process(WatchedEvent event) {
                try {
                    // 判断事件类型， 此处只处理子节点变化事件
                    if(event.getType() == Event.EventType.NodeChildrenChanged
                            && event.getPath().equals("/" + groupNode)) {
                        // 获取子节点， 并对父节点进行监听
                        List<String> children = zk.getChildren("/" + groupNode, true);
                        String thisNode = thisPath.substring(("/" + groupNode + "/").length());

                        Collections.sort(children);
                        if(children.indexOf(thisNode) == 0) {
                            doSomething();
                            thisPath = zk.create("/" + groupNode + "/" + subNode, null,
                                    Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        this.createGroupNode();

        // 初始化 thisPath
        thisPath = zk.create("/" + groupNode + "/" + subNode, null,
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        Thread.sleep(new Random().nextInt(1000));

        // 获取group下子节点, 并设置对group的监听
        List<String> childrenNodes = zk.getChildren("/" + groupNode, true);

        if(childrenNodes.size() == 1) {
            doSomething();
            thisPath = zk.create("/" + groupNode + "/" + subNode, null,
                    Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        }
    }

    /**
     * 处理业务逻辑, 处理完成后将thispath删除
     * @throws Exception
     */
    private void doSomething() throws Exception {
        try {
            //System.out.println("gain lock： " + thisPath);
            Thread.sleep(2000);
        }finally {
            //System.out.println("finished: " + thisPath);
            //
            zk.delete(this.thisPath, -1);
        }
    }

}