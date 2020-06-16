package com.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;


@EnableScheduling  //启动定时
@MapperScan("com.gmall.**")
@SpringBootApplication
public class GmallPassportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallPassportServiceApplication.class, args);
	}

}
