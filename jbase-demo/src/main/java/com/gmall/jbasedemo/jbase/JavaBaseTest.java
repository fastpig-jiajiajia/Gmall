package com.gmall.jbasedemo.jbase;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.sql.SQLSyntaxErrorException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

/**
 * @author rui.xu
 * @date 2020/09/17 10:08
 * @description
 **/
public class JavaBaseTest {

    public static void main(String[] args) {
        JavaBaseTest javaBaseTest = new JavaBaseTest();
//        javaBaseTest.parseDate();
//        javaBaseTest.parseJSON();
        javaBaseTest.add2Set();
    }


    /**
     * 转换只有时分秒字段的Date
     * fail
     */
    public void parseDate() {
        String time = "12:59:59";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        try{
            Date date = sdf.parse(time);
            System.out.println(sdf1.format(sdf1));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 转换JSON
     * success
     */
    public void parseJSON(){
        String jsonString = "[{\"labelName\":\"1\",\"status\":1},{\"labelName\":\"2\",\"status\":0}]";
        List<JSONObject> jsonList = JSONObject.parseArray(jsonString, JSONObject.class);
        jsonList.forEach(e -> System.out.println(e.toJSONString()));
    }


    public void add2Set(){
        Set<String> set = new HashSet<>();
        System.out.println(set.add("1"));
        System.out.println(set.add("2"));
        System.out.println(set.add("1"));
    }
}
