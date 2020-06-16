package com.gmall.zookeeperdemo.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.gmall.zookeeperdemo.zklock.ZooKeeperSession;
import com.sun.org.glassfish.external.statistics.impl.TimeStatisticImpl;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@RestController()
@RequestMapping("/zkController")
public class ZkController {


    ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 30, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());

    @RequestMapping("/getZKLock/id")
    public String getZKLock(@PathVariable("id") String id) {

        for(int i = 0; i < 20; i++){
            executor.execute(new Thread(() -> {
                ZooKeeperSession zooKeeperSession = ZooKeeperSession.getInstance();
                if(zooKeeperSession.acquireDistributedLock(String.valueOf(id))){
                    System.out.println(Thread.currentThread().getName() + " 获取到锁");
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    zooKeeperSession.releaseDistributedLock(String.valueOf(id));
                }else{
                    System.out.println(Thread.currentThread().getName() + "没获取到锁");
                }

            }, "thread-" + i));
        }

        return null;
    }
}
