package com.gmall.jbasedemo.optionaldemo;

import java.util.Optional;

public class OptionalDemo {

    public static void main(String[] args) {
        User user = new User();
//        User user = null;
        // 如果是 null 执行 orElseThrow，否则就返回 user
//        User user1 = Optional.ofNullable(user).orElseThrow(() -> {
//            return new RuntimeException("11");
//        });

        // user 对象为 null 创建 对象
        User user2 = Optional.ofNullable(user).orElseGet(() -> createNewUser());
        // 不管 user 对象是不是 null 都创建对象，orElse 和 orElseGet 的区别，orElse 每次必执行，orElseGet 只在传入对象是 null 的时候执行
        User user3 = Optional.ofNullable(user).orElse(createNewUser());

        System.out.println(user2.getName());
        System.out.println(user3.getName());

        Thread thread = new Thread();
        Thread thread1 = new Thread(thread);
    }


    private static User createNewUser(){
        System.out.println("1111");
        return new User();
    }
}



class User{

    private String name = "1";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
