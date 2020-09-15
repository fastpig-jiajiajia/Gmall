package com.gmall.redis.redpackagedemo.basic;

public class Basic {
	public static String ip = "39.101.198.56";
	public static int port = 6379;
	public static int honBaoCount = 1000;

	public static int threadCount = 20;
	public static String hongBaoPoolKey = "hongBaoPoolKey"; //LIST类型来模拟红包池子
	public static String hongBaoDetailListKey = "hongBaoDetailListKey";//LIST类型，记录所有用户抢红包的详情
	public static String userIdRecordKey = "userIdRecordKey";//记录已经抢过红包的用户ID,防止重复抢
	
	/*
	 * KEYS[1]:hongBaoPool：                   //键hongBaoPool为List类型，模拟红包池子，用来从红包池抢红包
	 * KEYS[2]:hongBaoDetailList：//键hongBaoDetailList为List类型，记录所有用户抢红包的详情
	 * KEYS[3]:userIdRecord ：           //键userIdRecord为Hash类型，记录所有已经抢过红包的用户ID
	 * KEYS[4]:userid ：                              //模拟抢红包的用户ID
	 * 
	 * 
	 * jedis.eval(  Basic.getHongBaoScript,   4,    Basic.hongBaoPoolKey,  Basic.hongBaoDetailListKey,	Basic.userIdRecordKey,  userid);
	 *                      Lua脚本                                参数个数                  key[1]                     key[2]                       key[3]      key[4]                   
	*/
	//作业：
	public static String getHongBaoScript =
		            "if redis.call('hexists', KEYS[3], KEYS[4]) ~= 0 then\n"   + 
		                 "return nil\n" + 
		            "else\n"  +
		                  "local hongBao = redis.call('rpop', KEYS[1]);\n"  +
		            	  "if hongBao then\n"  +
			            	 "local x = cjson.decode(hongBao);\n"  +
			            	 "x['userId'] = KEYS[4];\n"  +
			            	 "local re = cjson.encode(x);\n"  +
			            	 "redis.call('hset', KEYS[3], KEYS[4], '1');\n"  +
			            	 "redis.call('lpush', KEYS[2], re);\n" + 
			            	 "return re;\n"  +
		                  "end\n"  +
		            "end\n"  +
		            "return nil";  
}
