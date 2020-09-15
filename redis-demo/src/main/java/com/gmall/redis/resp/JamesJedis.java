package com.gmall.redis.resp;

import java.io.IOException;
import java.net.Socket;

public class JamesJedis {

    /*
    *3
    $3
    SET
    $4
    name
    $6
    rehash
     */
    public static String set(Socket socket,String key, String value) throws IOException {
        StringBuffer str = new StringBuffer();
        str.append("*3").append("\r\n");
        str.append("$3").append("\r\n");
        str.append("SET").append("\r\n");
        str.append("$").append(key.getBytes().length).append("\r\n");
        str.append(key).append("\r\n");
        str.append("$").append(value.getBytes().length).append("\r\n");
        str.append(value).append("\r\n");
        socket.getOutputStream().write(str.toString().getBytes());
        byte[] response = new byte[2048];
        socket.getInputStream().read(response);
        return new String(response);

    }
    public static String get(Socket socket,String key) throws IOException {
        StringBuffer str = new StringBuffer();
        str.append("*2").append("\r\n");
        str.append("$3").append("\r\n");
        str.append("GET").append("\r\n");
        str.append("$").append(key.getBytes().length).append("\r\n");
        str.append(key).append("\r\n");
        socket.getOutputStream().write(str.toString().getBytes());
        byte[] response = new byte[2048];
        socket.getInputStream().read(response);
        return new String(response);
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("39.101.198.56",6379);
        set(socket,"shaka","loveStus");
        System.out.println(get(socket,"shaka"));
    }
}
