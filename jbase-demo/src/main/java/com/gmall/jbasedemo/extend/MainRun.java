package com.gmall.jbasedemo.extend;

import javax.annotation.security.RunAs;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainRun {

    public static void main(String[] args) {
        Son son = new Son();
        System.out.println(son.grandFather);
        Lock lock = new ReentrantLock();

        String a = "abc";
        String b = "a" + "bc";
        System.out.println(a == b);

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName());
        }, "thread1").start();

        TestThread thread2 = new TestThread();
        thread2.run();

    }
}

class TestThread implements Runnable{

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }
}
