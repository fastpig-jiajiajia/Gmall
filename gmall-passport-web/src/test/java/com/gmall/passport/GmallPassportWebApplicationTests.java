package com.gmall.passport;

import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
class GmallPassportWebApplicationTests {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	void contextLoads() {
		sendLoginMergeCartMessage();
	}

	@Test
	private void sendLoginMergeCartMessage(){
		String messageId = UUID.randomUUID().toString();
		String messageData = "topic message queue";
		String createTime = org.apache.http.client.utils.DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
		Map<String, Object> messageMap = new HashMap<>();
		messageMap.put("messageId", messageId);
		messageMap.put("messageData", messageData);
		messageMap.put("createTime", createTime);
		rabbitTemplate.convertAndSend("topicExchange", "gmall.#", messageMap);

	}
}
