package com.gmall.order.mq;

import com.gmall.entity.OmsOrder;
import com.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * 监听支付服务发送的 MQ 消息
 */
@Component
public class OrderServiceMqListener {

    @Autowired
    OrderService orderService;

    /**
     * 监听支付消息队列
     * @param mapMessage
     * @throws JMSException
     */
    @JmsListener(destination = "PAYMENT_SUCCESS_QUEUE",containerFactory = "jmsQueueListener")
    public void consumePaymentResult(MapMessage mapMessage) throws JMSException {

        String out_trade_no = mapMessage.getString("out_trade_no");

        // 更新订单状态业务
        System.out.println(out_trade_no);

        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(out_trade_no);

        orderService.updateOrder(omsOrder);

        System.out.println("11111111111111");


    }
}
