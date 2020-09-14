package com.gmall.redis.resp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//写一个伪的redis
public class ServerRedis {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6379);
            Socket rec = serverSocket.accept();
            byte[] result = new byte[2048];
            rec.getInputStream().read(result);
            System.out.println(new String(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
