package com.gmall.mybatisdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class MybatisDemoApplicationTests {

	public static void main(String[] args) {
		new Thread(() -> {
			System.out.println(1 + Thread.currentThread().getName());
		}).start();

		new Thread(() -> {
			System.out.println(2 + Thread.currentThread().getName());
		}).run();
	}

}
