package com.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling  // 开启 Job
@SpringBootApplication
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@EnableCaching
public class GmallUserWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallUserWebApplication.class, args);
	}

}
