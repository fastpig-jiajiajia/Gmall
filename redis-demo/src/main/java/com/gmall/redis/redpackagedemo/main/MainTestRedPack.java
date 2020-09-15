package com.gmall.redis.redpackagedemo.main;

import com.gmall.redis.redpackagedemo.redpack.GenRedPack;
import com.gmall.redis.redpackagedemo.redpack.GetRedPack;

public class MainTestRedPack {
	

	public static void main(String[] args) throws InterruptedException {
		GenRedPack.genHongBao();//初始化红包
		
		GetRedPack.getHongBao();//从红包池抢红包
	
	}
}