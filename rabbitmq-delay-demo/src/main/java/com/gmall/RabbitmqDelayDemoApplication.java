package com.gmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(value = "com.gmall.rabbitmq.mapper")
@SpringBootApplication
public class RabbitmqDelayDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqDelayDemoApplication.class, args);
	}

}
