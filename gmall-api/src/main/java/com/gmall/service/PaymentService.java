package com.gmall.service;

import com.gmall.entity.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    void savePaymentInfo(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);

    Map<String,Object> checkAlipayPayment(String out_trade_no);

    void sendDelayPaymentResultCheckQueue(String outTradeNo, Integer count);
}
