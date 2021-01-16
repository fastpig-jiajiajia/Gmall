package com.gmall.rabbitmq.delay.distributedtransaction.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gmall.rabbitmq.MQConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class UserConsumer {

    @RabbitHandler
    @RabbitListener(queues = MQConstant.TRANSACTION_QUEUE)
    public void userMQHandler(Message message, Channel channel){
        byte[] bytes = message.getBody();
        String msg = new String(bytes);
        try {
            if(StringUtils.isNotBlank(msg)){
                JSONObject jsonObject = JSON.parseObject(msg);
                System.out.println(msg);
                String out_trade_no = jsonObject.getString("out_trade_no");
                int count = jsonObject.getIntValue("count");

                // 进行业务的操作

                // 确认收到消息，只确认当前消费者的一个消息收到
                // 消息消费完手动确认消息，确认完消息才会从队列中移除
                // deliveryTag 表示该消息的index（long类型的数字）；
                // multiple 表示是否批量（true：将一次性ack所有小于deliveryTag的消息）；
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception e) {
            try{
                if (message.getMessageProperties().getRedelivered()) {
                    log.info("【cartWeb】消息已经回滚过，拒绝接收消息 ： {}" + msg);
                    // 拒绝消息，并且不再重新进入队列
                    //public void basicReject(long deliveryTag, boolean requeue)
//                    相比channel.basicNack，除了没有multiple批量确认机制之外，其他语义完全一样。
//                    如果channel.basicReject(8, true);表示deliveryTag=8的消息处理失败且将该消息重新放回队列。
//                    如果channel.basicReject(8, false);表示deliveryTag=8的消息处理失败且将该消息直接丢弃。
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                } else {
                    log.info("【CcartWeb】消息即将返回队列重新处理 ：{}" + msg);
                    //设置消息重新回到队列处理
                    // requeue表示是否重新回到队列，true重新入队
                    // deliveryTag（唯一标识 ID）：当一个消费者向 RabbitMQ 注册后，会建立起一个 Channel ，
                    // RabbitMQ 会用 basic.deliver 方法向消费者推送消息，这个方法携带了一个 delivery tag，
                    // 它代表了 RabbitMQ 向该 Channel 投递的这条消息的唯一标识 ID，是一个单调递增的正整数，
                    // delivery tag 的范围仅限于 Channel
                    //public void basicNack(long deliveryTag, boolean multiple, boolean requeue)
//                    如果channel.basicNack(8, true, true);表示deliveryTag=8之前未确认的消息都处理失败且将这些消息重新放回队列中。
//                    如果channel.basicNack(8, true, false);表示deliveryTag=8之前未确认的消息都处理失败且将这些消息直接丢弃。
//                    如果channel.basicNack(8, false, true);表示deliveryTag=8的消息处理失败且将该消息重新放回队列。
//                    如果channel.basicNack(8, false, false);表示deliveryTag=8的消息处理失败且将该消息直接丢弃。
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                }
            }catch (Exception e1){
                e.printStackTrace();
            }
        }
    }
}
