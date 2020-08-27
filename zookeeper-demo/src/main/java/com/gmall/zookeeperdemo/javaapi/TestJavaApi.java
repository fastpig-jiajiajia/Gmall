package com.gmall.zookeeperdemo.javaapi;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by VULCAN on 2018/11/7.
 */
public class TestJavaApi implements Watcher {

    private static final int SESSION_TIMEOUT = 10000;
    private static final String CONNECTION_STRING = "127.0.0.1:2181";
    private static final String ZK_PATH = "/leader";
    private ZooKeeper zk = null;

    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    /**
     * 创建ZK连接
     *
     * @param connectString  ZK服务器地址列表
     * @param sessionTimeout Session超时时间
     */
    public void createConnection(String connectString, int sessionTimeout) {
        this.releaseConnection();
        try {
            zk = new ZooKeeper(connectString, sessionTimeout, this);
            connectedSemaphore.await();
        } catch (InterruptedException e) {
            System.out.println("连接创建失败，发生 InterruptedException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("连接创建失败，发生 IOException");
            e.printStackTrace();
        }
    }

    /**
     * 关闭ZK连接
     */
    public void releaseConnection() {
        if (null != this.zk) {
            try {
                this.zk.close();
            } catch (InterruptedException e) {
                // ignore
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建节点
     *
     * @param path 节点path
     * @param data 初始数据内容
     * @return
     */
    public boolean createPath(String path, String data) {
        try {
            System.out.println("节点创建成功, Path: "
                    + this.zk.create(path, // 节点路径
                    data.getBytes(), // 节点内容
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //节点权限
                    CreateMode.EPHEMERAL) //节点类型
                    + ", content: " + data);
        } catch (KeeperException e) {
            System.out.println("节点创建失败，发生KeeperException");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("节点创建失败，发生 InterruptedException");
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 读取指定节点数据内容
     *
     * @param path 节点path
     * @return
     */
    public String readData(String path) {
        try {
            System.out.println("获取数据成功，path：" + path);
            return new String(this.zk.getData(path, false, null));
        } catch (KeeperException e) {
            System.out.println("读取数据失败，发生KeeperException，path: " + path);
            e.printStackTrace();
            return "";
        } catch (InterruptedException e) {
            System.out.println("读取数据失败，发生 InterruptedException，path: " + path);
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 更新指定节点数据内容
     *
     * @param path 节点path
     * @param data 数据内容
     * @return
     */
    public boolean writeData(String path, String data) {
        try {
            System.out.println("更新数据成功，path：" + path + ", stat: " +
                    this.zk.setData(path, data.getBytes(), -1));
        } catch (KeeperException e) {
            System.out.println("更新数据失败，发生KeeperException，path: " + path);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("更新数据失败，发生 InterruptedException，path: " + path);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除指定节点
     *
     * @param path 节点path
     */
    public void deleteNode(String path) {
        try {
            this.zk.delete(path, -1);
            System.out.println("删除节点成功，path：" + path);
        } catch (KeeperException e) {
            System.out.println("删除节点失败，发生KeeperException，path: " + path);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("删除节点失败，发生 InterruptedException，path: " + path);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        TestJavaApi sample = new TestJavaApi();

        sample.createConnection(CONNECTION_STRING, SESSION_TIMEOUT);
        if (sample.createPath(ZK_PATH, "我是节点初始内容")) {
            System.out.println();
            System.out.println("数据内容: " + sample.readData(ZK_PATH) + "\n");
            sample.writeData(ZK_PATH, "更新后的数据");
            System.out.println("数据内容: " + sample.readData(ZK_PATH) + "\n");
            sample.deleteNode(ZK_PATH);
        }

        sample.releaseConnection();
    }

    /**
     * 收到来自Server的Watcher通知后的处理。
     */
    @Override
    public void process(WatchedEvent event) {
        System.out.println("收到事件通知：" + event.getState() + "\n");
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }

    }

}