package com.gmall.zookeeperdemo.enjoy.template;

import java.util.HashMap;
import java.util.Map;

public abstract  class AbstractTemplate {


    public void shopping() {
        Map<String,Float>  cars = new HashMap();
        cars.put("电池",10f);
        cars.put("娃娃",20f);
        cars.put("打气筒",30f);

        //1.清点商品
        checkGoods(cars);
        //2计算价格
        float mony = calculation(cars);
        //支付
        if(pay(mony)) {
            //如果支付成功送货
            delivery();
        }
    }


    public void checkGoods(Map<String,Float>  cars) {
        if(cars!=null) {
            System.out.print("你购买的了：");
            for(String key:cars.keySet()) {
                System.out.print(key+" ");
            }
            System.out.println();
        }
    }

    public float calculation(Map<String,Float>  cars) {
        float result = 0;
        if(cars!=null) {
            for(String key:cars.keySet()) {
                result += cars.get(key);
            }
        }
        System.out.println("你总共应该支付："+result);

        //钱
        return  result;
    }

    public abstract boolean pay(Float money) ;

    public  void delivery() {
        System.out.println("请稍等，小哥哥正在送货！");
    }
}
