package com.gmall.service;

import com.gmall.entity.OmsOrder;

public interface OrderService {

    String checkTradeCode(String memberId, String tradeCode);

    String genTradeCode(String memberId);

    void saveOrder(OmsOrder omsOrder);
}
