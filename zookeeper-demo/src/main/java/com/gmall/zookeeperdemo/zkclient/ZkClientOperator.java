package com.gmall.zookeeperdemo.zkclient;

import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * ZkClient 增删改查操作
 */
public class ZkClientOperator {

    /** zookeeper地址 */
    static final String CONNECT_ADDR = "39.101.198.56:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 10000;//ms


    public static void main(String[] args) throws Exception {
       //ZkClient zkc = new ZkClient(new ZkConnection(CONNECT_ADDR), SESSION_OUTTIME);
        ZkClient zkc = new ZkClient(CONNECT_ADDR, SESSION_OUTTIME);

        //1. create and delete方法
        zkc.createEphemeral("/temp"); // 创建临时节点，临时节点不支持子节点
        zkc.createPersistent("/super/c1", true);  // 创建永久节点，true 没有父节点就创建父节点，false 如果没有父节点报错
        String tempNodeName = zkc.createEphemeralSequential("/temp", "tempValue");  // 创建临时顺序节点，前缀和其他节点相同也无关联。两者之间删除互不影响。
        String superNodeName = zkc.createPersistentSequential("/super", "superValue"); // 创建永久顺序节点，前缀和其他节点相同也无关联。两者之间删除互不影响。
        System.out.println("tempNodeName：" + tempNodeName);
        System.out.println("superNodeName：" + superNodeName);


        Thread.sleep(1000);
        zkc.delete("/temp");
        zkc.deleteRecursive("/super");  // 递归删除当前节点和其子节点
        zkc.delete(tempNodeName);
        zkc.delete(superNodeName);


        //2. 设置path和data 并且读取子节点和每个节点的内容
        zkc.createPersistent("/super", "1234");
        zkc.createPersistent("/super/c1", "c1内容");
        zkc.createPersistent("/super/c2", "c2内容");


        List<String> list = zkc.getChildren("/super");
        for(String p : list){
            System.out.println(p);  // 当前节点名称
            String rp = "/super/" + p;
            String data = zkc.readData(rp);  // 获取当前节点的值
            System.out.println("节点为：" + rp + "，内容为: " + data);
        }

        //3. 更新和判断节点是否存在
        zkc.writeData("/super/c1", "新内容");  //  更新节点值
        System.out.println(zkc.readData("/super/c1").toString());
        System.out.println(zkc.exists("/super/c1"));

//		4.递归删除/super内容
        zkc.deleteRecursive("/super");
    }
}
