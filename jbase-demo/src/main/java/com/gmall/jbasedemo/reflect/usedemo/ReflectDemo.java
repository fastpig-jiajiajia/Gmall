package com.gmall.jbasedemo.reflect.usedemo;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectDemo {

    public static void main(String[] args) {
        String className = "com.gmall.jbasedemo.reflect.usedemo.Coder";

        try {
            // 1. Class类的forName静态方法获取Class对象
            Class<?> cls = Class.forName(className);
            System.out.println("1、" + cls.toString());   // 输出 Coder static block     class com.gmall.jbasedemo.reflect.Coder

            // 2、包名，是否加载静态代码块，指定类加载器
            Class<?> coderCls = Class.forName(className, false, ClassLoader.getSystemClassLoader());
            System.out.println("2、" + coderCls.toString());  // 输出 class com.gmall.jbasedemo.reflect.Coder
            Coder coder = new Coder(); // 输出 Coder static block，这时加载类的静态代码块

            // 3、输出 class io.kzw.advance.csdn_blog.Coder
            System.out.println("3、" + Coder.class.toString());
            System.out.println("3、" + coder.getClass().toString());

            // 4、反射创建对象
            coder = Coder.class.newInstance(); // 无参构造器创建
            Constructor constructor = Coder.class.getConstructor(String.class, int.class);
            coder = (Coder) constructor.newInstance("xr", 24);
            System.out.println("4、" + coder.toString());

            // 5、获取方法
            Method[] declaredMethods = cls.getDeclaredMethods(); // 返回类或接口声明的所有方法，包括公共、保护、默认（包）访问和私有方法，但不包括继承的方法
            Method[] publicMethods = cls.getMethods(); // 返回某个类的所有公用（public）方法，包括其继承类的公用方法
            Method method = cls.getMethod("setAge", int.class);  // 参数：方法名+参数类型(可以多个) 获取公开的方法，private 方法获取会报错
            System.out.println("5、declaredMethods======" + declaredMethods.length);
            System.out.println("5、publicMethods======" + publicMethods.length);
            System.out.println("5、method======" + method);

            // 6、获取字段属性
            Field nameField = cls.getField("name");  // 访问公有的成员变量
            Field declaredField = cls.getDeclaredField("age");  // 所有已声明的成员变量，但不能得到其父类的成员变量
            Field[] fields = cls.getFields(); // 返回某个类的所有公用（public）字段，包括其继承类的公用字段
            Field[] declaredFields = cls.getDeclaredFields();// 返回类或接口声明的所有字段，包括公共、保护、默认（包）访问和私有字段，但不包括继承的字段
            System.out.println("6、field======" + nameField.getName());
            System.out.println("6、declaredMethods======" + declaredField.getName());
            System.out.println("6、declaredMethods======" + fields.length);
            System.out.println("6、declaredMethods======" + declaredFields.length);

            // 7、反射调用方法
            Method setNameMethod = cls.getDeclaredMethod("setName", String.class);
            setNameMethod.setAccessible(true);// 设权限可以访问
            setNameMethod.invoke(coder, "xr Invoke");
            System.out.println("7、" + coder.toString());

            // 8、反射修改静态非基本类型和String的常量
            Field numField = cls.getField("NUM");
            numField.setAccessible(true);
            // 反射调用 final 和static 必须使用这段代码，不然会报错
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(numField, numField.getModifiers() &~ Modifier.FINAL);
            numField.set(coder, 200);
            System.out.println("8、num = " + Coder.NUM);
            nameField.set(coder, "nameFiled set");
            System.out.println("9、num = " + coder.name);

            Field countField = cls.getDeclaredField("COUNT");
            countField.setAccessible(true);
            Field modifiers = countField.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(countField, countField.getModifiers() & ~Modifier.FINAL);
            countField.set(coder, 5);
            System.out.println("8、count = " + coder.getCOUNT());


            Object obj = new Object();
            WeakReference<Object> objWeakReference = new WeakReference<>(obj);
            System.gc();



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


class Coder {

    public String name;
    private int age;
    public final static Integer NUM = 100;
    private final static Integer COUNT = 50;


    static {
        System.out.println("Coder static block");
    }

    public Coder(){

    }

    public Coder(String name, int age){
        this.name = name;
        this.age =  age;
    }


    public static String getNameAndAge(){
        return "getNameAndAge";
    }

    private String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getNUM() {
        return NUM;
    }

    public int getCOUNT() {
        return COUNT;
    }

    @Override
    public String toString() {
        return "Coder{" + "name='" + name + '\'' + ", age=" + age + ", num=" + NUM + ", count=" + COUNT + '}';
    }
}
