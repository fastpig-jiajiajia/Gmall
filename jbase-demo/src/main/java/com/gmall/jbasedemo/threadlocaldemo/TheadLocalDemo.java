package com.gmall.jbasedemo.threadlocaldemo;

import java.sql.Time;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author xurui
 */
public class TheadLocalDemo {
    public static void main(String[] args) {
        demo2();
    }


    public static void demo1(){
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10, () -> {
            System.out.println(Thread.currentThread().getName() + "111=====" + threadLocal.get());
        });

        IntStream.range(0, 10).forEach((data) -> {
            Random random = new Random();
            new Thread(() -> {
                threadLocal.set(String.valueOf(random.nextInt(100)));
                threadLocal.set("dWQ");
                try {
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() + "=====" + threadLocal.get());
                    threadLocal.remove();   // 每个线程都需要单独调用，只会清除当前线程的值
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private static void demo2(){
        ThreadLocal<String> threadLocal = new ThreadLocal<>();

        new Thread(() -> {
            threadLocal.set("1111");
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=====" + threadLocal.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "thread-1").start();

        new Thread(() -> {
            threadLocal.set("2222");
            threadLocal.remove();
            System.out.println(Thread.currentThread().getName() + "=====" + threadLocal.get());
        }, "thread-2").start();
    }




}
