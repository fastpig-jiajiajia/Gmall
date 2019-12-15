package com.gmall.cart.mq;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.service.CartService;
import com.gmall.util.HttpclientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.HashMap;
import java.util.Map;

@Component
public class CartActiveMQConsumer {
    @Reference
    private CartService cartService;

    @JmsListener(destination = "LOGIN_MERGECART_QUEUE", containerFactory = "jmsQueueListener")
    public void mergeCart(MapMessage message) {
        try {
            String tradeNo = message.getString("tradeNo");
            int count = message.getInt("count");
            System.out.println("tradeNo: " + tradeNo + "       count:" + count--);
            if(count > 0){
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("tradeNo", tradeNo);
                paramMap.put("count", String.valueOf(count));
                HttpclientUtil.doPost("http://passport.gmall.com:8085/sendLoginMergeCartMessageActiveMQ", paramMap);
            }else{
                cartService.mergeCart(null, null);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
