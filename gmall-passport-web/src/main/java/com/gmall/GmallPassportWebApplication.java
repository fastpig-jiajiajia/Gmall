package com.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAuthorizationServer  // spring securoty oauth2 server
//@EnableResourceServer
// @EnableConfigurationProperties(RsaKeyProperties.class)
public class GmallPassportWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallPassportWebApplication.class, args);
	}

}
