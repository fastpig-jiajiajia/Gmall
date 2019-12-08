package com.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gmall")
@MapperScan(basePackages = "com.gmall.**")
public class GmallInventoryManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallInventoryManageApplication.class, args);
	}

}
