package com.gmall.jbasedemo.jvm;

public class JvmMemoryArea {

    private int i = 1;  // 引用：堆，值：常量池
    private Integer i1 = 1;
    private Integer i2 = 128;
    private Integer i3 = new Integer(2);
    private static Integer i4 = new Integer(2);
    private final static Integer i5 = new Integer(2);

    private String str1 = "String";
    private String str2 = new String("String");
    private static String str3 = "String";
    private final static String STR = "String";

    private JvmMemoryArea areaClass = new JvmMemoryArea();
    private static JvmMemoryArea areaClass1 = new JvmMemoryArea();
    private final static JvmMemoryArea AREA_CLASS = new JvmMemoryArea();

    private void testMethod(){
        int j = 0;
        Integer j1 = new Integer(1);
        JvmMemoryArea jvmMemoryArea = new JvmMemoryArea();
    }

    private static void testMethod(Integer num){
        int j = 0;
        Integer j1 = new Integer(1);
    }
}

