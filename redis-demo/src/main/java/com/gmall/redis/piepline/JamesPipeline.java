package com.gmall.redis.piepline;

import java.net.Socket;
/**
 * 自定义pipeline类
 * 
 * @author 【享学课堂】 James老师 qq ：1076258117
 * @author 【享学课堂】 架构技术QQ群 ：684504192
 * @author 【享学课堂】 往期视频依娜老师 ：2470523467
 */
public class JamesPipeline {
	private Socket socket;

	public JamesPipeline(Socket socket) {
		this.socket = socket;
	}
	/**
	 * 传入数组KEY，批量删除
	 * 
	 * @author 【享学课堂】 James老师 qq ：1076258117
	 * @author 【享学课堂】 架构技术QQ群 ：684504192
	 * @author 【享学课堂】 往期视频依娜老师 ：2470523467
	 * String[]{"key:0","key:1","key:2","key:3","key:4"})
	 */
	public void mdel(String... keys) throws Exception {
		
		StringBuffer str = new StringBuffer();
		for(String key:keys){
			str.append("*2").append("\r\n");
			str.append("$3").append("\r\n");
			str.append("del").append("\r\n");
			str.append("$").append(key.getBytes().length).append("\r\n");
			str.append(key).append("\r\n");
		}
	
		socket.getOutputStream().write(str.toString().getBytes());
	}


	/**
	 * 获取从Redis服务端响应的结果
	 * 
	 * @author 【享学课堂】 James老师 qq ：1076258117
	 * @author 【享学课堂】 架构技术QQ群 ：684504192
	 * @author 【享学课堂】 往期视频依娜老师 ：2470523467
	 */
	public String resp() throws Exception {
		byte[] b = new byte[2048];
		socket.getInputStream().read(b );
		return  new String(b);
	}

}
