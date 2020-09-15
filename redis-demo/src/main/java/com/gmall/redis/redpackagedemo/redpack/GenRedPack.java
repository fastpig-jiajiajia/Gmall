package com.gmall.redis.redpackagedemo.redpack;

import java.util.concurrent.CountDownLatch;

import com.alibaba.fastjson.JSONObject;
import com.gmall.redis.redpackagedemo.basic.Basic;
import com.gmall.redis.redpackagedemo.utils.JedisUtils;

public class GenRedPack {
	/**
	 * 多线程模拟红包池初始化  Jedis类
	 */
	public static void genHongBao() throws InterruptedException {
		JedisUtils jedis = new JedisUtils(Basic.ip, Basic.port, Basic.auth);
		jedis.flushall();  //清空,线上不要用.....

		//发枪器
		final CountDownLatch latch = new CountDownLatch(Basic.threadCount);
		
		for(int i = 0 ; i < Basic.threadCount; i++){
			final int page = i;
			Thread thread = new Thread(){
				public void run(){
					latch.countDown();
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//每个线程要初始化多少个红包  20线程
					int per = Basic.honBaoCount/Basic.threadCount;
					
					JSONObject object = new JSONObject();
					
					for(int j = page * per ; j < (page+1) * per; j++){
						object.put("id", "rid_"+j); //红包ID
						object.put("money", j);   //红包金额
						jedis.lpush("hongBaoPoolKey", object.toJSONString());
					}
				}
			};
			thread.start();
		}
	}
}
