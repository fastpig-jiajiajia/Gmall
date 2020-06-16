package com.gmall.zookeeperdemo;

import com.gmall.zookeeperdemo.zklock.ZooKeeperSession;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

public class ZookeeperDemoApplicationTests {

	static ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 30, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());

	private final static int id = 11;

	public static void main(String[] args) throws InterruptedException {
		int[] flagArray = new int[21];



		CyclicBarrier cyclicBarrier = new CyclicBarrier(20, () -> {
			System.out.println(" do all done !!!");
		});
		for(int i = 0; i < 20; i++){
			executor.submit(new Thread(() -> {
				ZooKeeperSession zooKeeperSession = ZooKeeperSession.getInstance();
				if(zooKeeperSession.acquireDistributedLock(String.valueOf(id))){
					System.out.println(Thread.currentThread().getName() + " 获取到锁");
					int num = Integer.parseInt(Thread.currentThread().getName().substring(14));
					flagArray[num] = 1;
					StringBuilder sb = new StringBuilder("");
					for(int j = 0; j < flagArray.length; j++){
						if(flagArray[j] == 0){
							sb.append(" 线程-" + j + " 未获取到锁 \t");
						}
					}
					System.out.println(sb.toString());
//					try {
//						TimeUnit.SECONDS.sleep(1);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					zooKeeperSession.releaseDistributedLock(String.valueOf(id));
				}else{
					System.out.println(Thread.currentThread().getName() + "没获取到锁");
				}

				try {
					cyclicBarrier.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			}, "thread-" + i));
		}

		executor.shutdown();
		// 超时强制关闭
		executor.awaitTermination(180,  TimeUnit.SECONDS);
		while (true) {
			if (executor.isTerminated()) {
				System.out.println("生成成功！");
				break;
			}
		}
	}

}
