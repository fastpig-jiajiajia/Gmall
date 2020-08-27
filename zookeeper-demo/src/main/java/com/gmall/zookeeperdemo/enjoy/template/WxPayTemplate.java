package com.gmall.zookeeperdemo.enjoy.template;


//实现支付方式
public class WxPayTemplate  extends AbstractTemplate{
    public boolean pay(Float money) {
        System.out.println("Deer使用微信支付了:"+money);
        return true;
    }

    public static void main(String[] args) {
       AbstractTemplate at = new WxPayTemplate();
       at.shopping();
    }
}
