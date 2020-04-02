package com.gmall;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GmallManageWebApplicationTests {

	@Test
	void contextLoads() throws IOException, MyException {
		// 配置 fdfs 的全局链接地址
		String tracker = GmallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();
		// 初始化连接
		ClientGlobal.init(tracker);

		TrackerClient trackerClient = new TrackerClient();
		// 获得一个 TrackerServer 实例
		TrackerServer trackerServer = new TrackerClient().getTrackerServer();

		StorageClient storageClient = new StorageClient(trackerServer, null);

		// meta_list: 文件的属性列表
		String[] upLoadFiles = storageClient.upload_file("C:\\Users\\Administrator\\Desktop/1.png", "png", null);

		String url = "http://39.101.198.56";
		for(String str : upLoadFiles){
			url += "/" +  str;
		}
		System.out.println(url);
	}

}
