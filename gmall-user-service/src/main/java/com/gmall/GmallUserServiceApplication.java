package com.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.gmall.**")
@EnableCaching  // 开启 ecahce 缓存
public class GmallUserServiceApplication {

	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}
	public static void main(String[] args) {
		SpringApplication.run(GmallUserServiceApplication.class, args);
	}

}
