package com.gmall.refresh.service;

import com.gmall.refresh.constant.RefreshConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author rui.xu
 * @date 2020/12/04 17:27
 * @description
 **/
@Scope(RefreshConstant.SCOPE_NAME)
@Component
public class RefreshService {

    @Value("${order.orderNo}")
    private String orderNo;


    public void doService() {
        System.out.println("doService: " + orderNo);
    }

}