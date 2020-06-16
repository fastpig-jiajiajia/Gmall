package com.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.gmall.GmallConstant;
import com.gmall.entity.PaymentInfo;
import com.gmall.payment.mapper.PaymentInfoMapper;
import com.gmall.service.PaymentService;
import com.gmall.util.ActiveMQUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AlipayClient alipayClient;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    /**
     * 更新支付状态信息，并向订单服务发动消息，更改订单状态。
     * @param paymentInfo
     */
    @Override
    @Transactional
    public void updatePayment(PaymentInfo paymentInfo) {
        // 幂等性检查
        PaymentInfo paymentInfoParam = new PaymentInfo();
        paymentInfoParam.setOrderSn(paymentInfo.getOrderSn());
        PaymentInfo paymentInfoResult =paymentInfoMapper.selectOne(paymentInfoParam);

        if(StringUtils.isNotBlank(paymentInfoResult.getPaymentStatus())&&paymentInfoResult.getPaymentStatus().equals("已支付")){
            return;
        }else{

            String orderSn = paymentInfo.getOrderSn();

            Example e = new Example(PaymentInfo.class);
            e.createCriteria().andEqualTo("orderSn",orderSn);

            Connection connection = null;
            Session session = null;
            try {
                connection = activeMQUtil.getConnectionFactory().createConnection();
                session = connection.createSession(true, Session.SESSION_TRANSACTED);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            try{
                paymentInfoMapper.updateByExampleSelective(paymentInfo,e);
                // 支付成功后，引起的系统服务-》订单服务的更新-》库存服务-》物流服务
                // 调用mq发送支付成功的消息
                Queue payhment_success_queue = session.createQueue("PAYHMENT_SUCCESS_QUEUE");
                MessageProducer producer = session.createProducer(payhment_success_queue);

                //TextMessage textMessage=new ActiveMQTextMessage();//字符串文本

                MapMessage mapMessage = new ActiveMQMapMessage();// hash结构

                mapMessage.setString("out_trade_no",paymentInfo.getOrderSn());

                producer.send(mapMessage);

                session.commit();
            }catch (Exception ex){
                // 消息回滚
                try {
                    session.rollback();
                } catch (JMSException e1) {
                    e1.printStackTrace();
                }
            }finally {
                try {
                    connection.close();
                } catch (JMSException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查是否已经支付，或者是交易创建状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, Object> checkAlipayPayment(String out_trade_no) {
        Map<String,Object> resultMap = new HashMap<>();

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("out_trade_no",out_trade_no);
        request.setBizContent(JSON.toJSONString(requestMap));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            System.out.println("有可能交易已创建，调用成功");
            resultMap.put("out_trade_no",response.getOutTradeNo());
            resultMap.put("trade_no",response.getTradeNo());
            resultMap.put("trade_status",response.getTradeStatus());
            resultMap.put("call_back_content",response.getMsg());
        } else {
            System.out.println("有可能交易未创建，调用失败");

        }

        return resultMap;
    }

    /**
     * rabbitMQ 支付检查延迟队列，采用插件的方式
     */
    public String sendDelayPaymentResultCheckQueueRabbitMQ(String outTradeNo, Integer count){
        com.rabbitmq.client.Connection connection = null;
        Channel channel = null;

        try { // 创建连接
            com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
            factory.setHost("39.101.198.56");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/gmall");
            connection = factory.newConnection();
            // 定义信道
            channel = connection.createChannel();
            // 定义交换机名称
            String exchange_name = "GMALL_PAYMENT_DELAY_EXCHANGE";
            // 声明交换机，fanout是定义发布订阅模式  direct是 路由模式 topic是主题模式, x-delayed-message 配合插件实现延迟队列
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-delayed-type", "direct");
            channel.exchangeDeclare(exchange_name, "x-delayed-message", true, false, arguments);
            channel.queueDeclare("GMALL_PAYMENT_DELAY_QUEUE", true, false, false, null);

            // 设置定时时间
            Map<String, Object> headers = new HashMap<>();
            //延迟10s后发送
            headers.put("x-delay", 10 * 1000);
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.headers(headers);


            String messageId = UUID.randomUUID().toString();
            String createTime = org.apache.http.client.utils.DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("messageId", messageId);
            messageMap.put("outTradeNo", outTradeNo);
            messageMap.put("count", count);
            messageMap.put("createTime", createTime);

            channel.basicPublish("GMALL_PAYMENT_DELAY_EXCHANGE", "GMALL_PAYMENT_DELAY_QUEUE", builder.build(), JSONObject.toJSONString(messageMap).getBytes());
            CorrelationData correlationData = new CorrelationData();
            correlationData.setId(UUID.randomUUID().toString());
            rabbitTemplate.convertAndSend(exchange_name, "gmall.#",  JSONObject.toJSONString(messageMap), correlationData);

            // 开启发送方确认模式
            channel.confirmSelect();
//            channel.basicPublish(exchange_name, "gmall.#", null, JSONObject.toJSONString(messageMap).getBytes());
            // 异步监听确认和未确认的消息
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleNack(long deliveryTag, boolean multiple) {
                    System.out.println("未确认消息，标识：" + deliveryTag);
                }

                @Override
                public void handleAck(long deliveryTag, boolean multiple)  {
                    System.out.println(String.format("已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple));
                }
            });

            // 普通 方式监听确认
            if (channel.waitForConfirms()) {
                System.out.println("消息发送成功" );
                return GmallConstant.SUCCESS;
            }else{
                return GmallConstant.FAIL;
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            // 关闭连接
            try {
                if(channel != null){
                    channel.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return GmallConstant.FAIL;
    }


    /**
     * activemq 支付检查延迟队列
     * @param outTradeNo
     * @param count
     */
    @Override
    public void sendDelayPaymentResultCheckQueue(String outTradeNo, Integer count) {
        Connection connection = null;
        Session session = null;
        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try{
            Queue payhment_success_queue = session.createQueue("PAYMENT_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(payhment_success_queue);

            //TextMessage textMessage=new ActiveMQTextMessage();//字符串文本

            MapMessage mapMessage = new ActiveMQMapMessage();// hash结构

            mapMessage.setString("out_trade_no",outTradeNo);
            mapMessage.setInt("count", count);

            // 为消息加入延迟时间
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*60);

            producer.send(mapMessage);

            session.commit();
        }catch (Exception ex){
            // 消息回滚
            try {
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }finally {
            try {
                connection.close();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }
    }
}
