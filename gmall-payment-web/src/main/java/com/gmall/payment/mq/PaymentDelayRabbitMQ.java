package com.gmall.payment.mq;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gmall.entity.PaymentInfo;
import com.gmall.service.CartService;
import com.gmall.service.PaymentService;
import com.gmall.util.RedisUtil;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Map;

public class PaymentDelayRabbitMQ {

    @Reference
    PaymentService paymentService;

    private static final Logger logger = Logger.getLogger(PaymentDelayRabbitMQ.class);

    /**
     * 消息一旦进入方法，就会变为 unchecked 状态，在开启手动确认模式下，如果消费失败，没有进行确认就会重新进入队列，重新变为未消费状态。
     * @param message
     * @param channel
     */
    @RabbitHandler
    @RabbitListener(queues = "LOGIN_MERGECART_QUEUE")
    public void rabbitMQConsumer(Message message, Channel channel){
        byte[] bytes = message.getBody();
        String msg = new String(bytes);
        try {
            if(StringUtils.isNotBlank(msg)){
                JSONObject jsonObject = JSON.parseObject(msg);
                System.out.println(msg);
                String out_trade_no = jsonObject.getString("out_trade_no");
                int count = jsonObject.getIntValue("count");

                // 调用paymentService的支付宝检查接口
                System.out.println("进行延迟检查，调用支付检查的接口服务");
                Map<String,Object> resultMap = paymentService.checkAlipayPayment(out_trade_no);

                if(resultMap!=null&&!resultMap.isEmpty()){
                    String trade_status = (String)resultMap.get("trade_status");
                    // 根据查询的支付状态结果，判断是否进行下一次的延迟任务还是支付成功更新数据和后续任务
                    if(StringUtils.isNotBlank(trade_status) && trade_status.equals("TRADE_SUCCESS")){
                        // 支付成功，更新支付发送支付队列
                        PaymentInfo paymentInfo = new PaymentInfo();
                        paymentInfo.setOrderSn(out_trade_no);
                        paymentInfo.setPaymentStatus("已支付");
                        paymentInfo.setAlipayTradeNo((String)resultMap.get("trade_no"));// 支付宝的交易凭证号
                        paymentInfo.setCallbackContent((String)resultMap.get(("call_back_content")));//回调请求字符串
                        paymentInfo.setCallbackTime(new Date());

                        System.out.println("已经支付成功，调用支付服务，修改支付信息和发送支付成功的队列");
                        paymentService.updatePayment(paymentInfo);
                        return;
                    }
                }

                if(count > 0){
                    // 继续发送延迟检查任务，计算延迟时间等
                    System.out.println("没有支付成功，检查剩余次数为"+count+",继续发送延迟检查任务");
                    count--;
                    paymentService.sendDelayPaymentResultCheckQueueRabbitMQ(out_trade_no, count);
                }else{
                    System.out.println("检查剩余次数用尽，结束检查");
                }

                logger.info("【cartWeb成功接收到消息】>>> {}" + msg);

                // 确认收到消息，只确认当前消费者的一个消息收到
                // 消息消费完手动确认消息，确认完消息才会从磁盘中移除
                // deliveryTag 表示该消息的index（long类型的数字）；
                // multiple 表示是否批量（true：将一次性ack所有小于deliveryTag的消息）；
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception e) {
            try{
                if (message.getMessageProperties().getRedelivered()) {
                    logger.info("【cartWeb】消息已经回滚过，拒绝接收消息 ： {}" + msg);
                    // 拒绝消息，并且不再重新进入队列
                    //public void basicReject(long deliveryTag, boolean requeue)
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                } else {
                    logger.info("【CcartWeb】消息即将返回队列重新处理 ：{}" + msg);
                    //设置消息重新回到队列处理
                    // requeue表示是否重新回到队列，true重新入队
                    //public void basicNack(long deliveryTag, boolean multiple, boolean requeue)
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                }
            }catch (Exception e1){
                e.printStackTrace();
                e.printStackTrace();
            }
        }
    }

}
