package com.gmall.jbasedemo.iterator;

import ch.qos.logback.core.net.SyslogOutputStream;

import java.util.*;

public class IteratorTest {


    public static void main(String[] args) {
        List<Person> personList = new ArrayList<>(10);
        personList.add(new Person("tom1"));
        personList.add(new Person("tom2"));
        personList.add(new Person("tom3"));
        personList.add(new Person("tom4"));
        personList.add(new Person("tom5"));
        personList.add(new Person("tom6"));
        personList.add(new Person("tom7"));
        personList.add(new Person("tom8"));
        personList.add(new Person("tom9"));
        personList.add(new Person("tom10"));

//        try{
//            // 报错 ConcurrentModificationException
//            for(Person person : personList){
//                if("tom3".equals(person.name)){
//                    personList.remove(person);
//                }
//            }
//        }catch (ConcurrentModificationException e){
//            e.printStackTrace();
//        }

        // 无报错 向前移位
//        for(int i = 0; i < personList.size(); i++){
//            Person person = personList.get(i);
//            System.out.println(person.name);
//            if("tom3".equals(person.name)){
//                personList.remove(person);
//            }
//        }

        Iterator<Person> iterator = personList.iterator();
        while (iterator.hasNext()){
            Person person = iterator.next();
            System.out.println(person.name);
            // 报错 ConcurrentModificationException
//            if("tom3".equals(person.name)){
//                personList.remove(person);
//            }
            if ("tom3".equals(person.name)) {
                iterator.remove();
            }
        }

        System.out.println("============================");

        for(Person person : personList){
            System.out.println(person.name);
        }

        System.out.println("============================");

        // sublist
        // 还是会改变原来的集合
    //    List<Person> personArrayList = new ArrayList<>(personList.subList(0, 5));
        List<Person> personArrayList = Arrays.asList((Person) personList.subList(0, 5));
        for(Person person : personArrayList){
            person.name = "212";
        }

        for(Person person : personList){
            System.out.println(person.name);
        }



        Map<String, String> map = new HashMap<>();
        map.entrySet().iterator();

    }
}



class Person{
    String name;

    public Person(){ }

    public Person(String name){
        this.name = name;
    }


}



