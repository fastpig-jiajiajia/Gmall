package com.gmall.jbasedemo.juc.productor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class PCTest {

    private static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(8);

    public static void main(String[] args) {

        new Thread(() -> {
            for(int i = 0; i <= 20; i++){
                try {
                    queue.offer(String.valueOf(i), 1, TimeUnit.SECONDS);
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        new Thread(() -> {

            while (true){
                try {
                //    System.out.println(queue.poll(1, TimeUnit.SECONDS));
                    System.out.println(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
