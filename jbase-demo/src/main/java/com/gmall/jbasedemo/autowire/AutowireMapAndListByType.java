package com.gmall.jbasedemo.autowire;

import com.gmall.jbasedemo.utils.BeanUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author rui.xu
 * @date 2020/09/22 15:34
 * @description 将类自动装配为 集合
 **/
@Component
public class AutowireMapAndListByType {

    @Autowired
    private ServiceClass1 serviceClass1;
    @Autowired
    private ServiceClass2 serviceClass2;

    @Autowired
    private List<BaseInterface> serviceClassList;
    @Autowired
    private Map<String, BaseInterface> serviceClassMap;

    private final static String XML_PATH = "spring-dev/spring.xml";


    @Test
    public void test() {
//        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(XML_PATH);
//        AutowireMapAndListByType autowireMapAndListByType = (AutowireMapAndListByType) applicationContext.getBean("autowireMapAndListByType");
//        System.out.println(autowireMapAndListByType.toString());

        serviceClass1.print();
        serviceClass2.print();
        ServiceClass1 serviceClass1 = (ServiceClass1) BeanUtils.getBean("serviceClass1");
        serviceClass1.print();

        ((ServiceClass1)serviceClassList.get(0)).print();
        ((ServiceClass1)serviceClassMap.get("serviceClass1")).print();
//        ServiceClass1 serviceClass1 = (ServiceClass1) applicationContext.getBean("serviceClass1");
//        serviceClass1.print();

//        ApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
//        AutowireMapAndListByType autowireMapAndListByType1 = (AutowireMapAndListByType) annotationConfigApplicationContext.getBean("serviceClass2");
//        System.out.println(autowireMapAndListByType1.toString());
    }


//    public void setServiceClass1(ServiceClass1 serviceClass1) {
//        this.serviceClass1 = serviceClass1;
//    }
//
//    public void setServiceClass2(ServiceClass2 serviceClass2) {
//        this.serviceClass2 = serviceClass2;
//    }

//    public void setServiceClassList(List<BaseInterface> serviceClassList) {
//        this.serviceClassList = serviceClassList;
//    }
//
//    public void setServiceClassMap(Map<String, BaseInterface> serviceClassMap) {
//        this.serviceClassMap = serviceClassMap;
//    }

    @Override
    public String toString() {
        return "AutowireMapAndListByType{" +
                "serviceClass1=" + serviceClass1 +
                ", serviceClass2=" + serviceClass2 +
                ", serviceClassList=" + serviceClassList +
                ", serviceClassMap=" + serviceClassMap +
                '}';
    }
}

