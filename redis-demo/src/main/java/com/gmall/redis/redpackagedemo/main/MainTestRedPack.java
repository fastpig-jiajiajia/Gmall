package com.gmall.redis.redpackagedemo.main;

import com.james.cache.redpack.GenRedPack;
import com.james.cache.redpack.GetRedPack;

public class MainTestRedPack {
	

	public static void main(String[] args) throws InterruptedException {
		GenRedPack.genHongBao();//初始化红包
		
		GetRedPack.getHongBao();//从红包池抢红包
	
	}
}