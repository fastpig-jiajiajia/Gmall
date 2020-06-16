package com.gmall.jbasedemo.juc;


import sun.util.resources.th.CalendarData_th;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintThread {

    public static void main(String[] args) {
        Print print = new Print();

        new Thread(() -> {
            for(int i = 0; i < 5; i++){
                print.printA();
            }
        }, "thread-A").start();

        new Thread(() -> {
            for(int i = 0; i < 5; i++){
                print.printB();
            }
        }, "thread-B").start();

        new Thread(() -> {
            for(int i = 0; i < 5; i++){
                print.printC();
            }

        }, "thread-C").start();
    }



}


class Print{

    Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();
    AtomicInteger status = new AtomicInteger(1);


    public void printA(){
        lock.lock();
        try{
            while(status.get() != 1){
                condition1.await();
            }

            System.out.println(Thread.currentThread().getName());
            for(int i = 1; i <= 2; i++){
                System.out.println("A" + i);
            }

            status.set(2);
            condition2.signal();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }


    public void printB(){
        lock.lock();
        try{
            while(status.get() != 2){
                condition2.await();
            }

            System.out.println(Thread.currentThread().getName());
            for(int i = 1; i <= 4; i++){
                System.out.println("B" + i);
            }

            status.set(3);
            condition3.signal();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }


    public void printC(){
        lock.lock();

        try{
            while (status.get() != 3){
                condition3.await();
            }

            System.out.println(Thread.currentThread().getName());
            for(int i = 1; i <= 6; i++){
                System.out.println("C" + i);
            }

            status.set(1);
            condition1.signal();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }




}
