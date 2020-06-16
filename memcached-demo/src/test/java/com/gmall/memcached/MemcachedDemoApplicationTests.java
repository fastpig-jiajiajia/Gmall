package com.gmall.memcached;

import com.gmall.MemcachedDemoApplication;
import com.gmall.memcached.config.MemcachedRunner;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = MemcachedDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)  //classes = MemcachedDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
public class MemcachedDemoApplicationTests {

    @Autowired
    private MemcachedClient client;

	@Test
	void contextLoads() {
        // 放入缓存, 如下参数key为name，值为louis，过期时间为5000，单位为毫秒
        OperationFuture<Boolean> flag = client.set("111", 3000,"222");
		System.out.println("set  " + flag.getStatus());  // set  {OperationStatus success=true:  STORED}

        // 改变一个存在的KEY值 ，但它还带了检查的功能
        client.cas("111", 3, "555");

		// 如果 key 存在就添加失败
        System.out.println(client.add("111", 5000, "222").getStatus());  //{OperationStatus success=false:  NOT_STORED}

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("get  " +  client.get("111"));

	}

}
