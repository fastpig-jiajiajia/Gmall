package com.gmall.zookeeperdemo.enjoy.simple;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by VULCAN on 2018/9/20.
 */
public class OrderNumGenerator {
    //全局订单id
    public  static int count = 0;

    //生成订单ID
    public   String getNumber() {
        SimpleDateFormat simpt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return simpt.format(new Date()) + "-" + ++count;
    }

}
