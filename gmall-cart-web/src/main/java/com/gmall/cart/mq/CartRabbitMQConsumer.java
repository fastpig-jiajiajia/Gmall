package com.gmall.cart.mq;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gmall.service.CartService;
import com.gmall.util.RedisUtil;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 * Consumer 登录合并购物车；消费者接受消息
 *
 * @author xurui
 *
 */
@Component
public class CartRabbitMQConsumer {

    @Reference
    private CartService cartService;

    @Autowired
    private RedisUtil redisUtil;

    private static final Logger logger = Logger.getLogger(CartRabbitMQConsumer.class);

    /**
     * 消息一旦进入方法，就会变为 unchecked 状态，在开启手动确认模式下，如果消费失败，没有进行确认就会重新进入队列，重新变为未消费状态。
     * 再次启动时会被重新消费到
     * @param message
     * @param channel
     */
    @RabbitHandler
    @RabbitListener(queues = "LOGIN_MERGECART_QUEUE")
    public void rabbitMQConsumer(Message message, Channel channel){
        Jedis jedis = redisUtil.getJedis();
        byte[] bytes = message.getBody();
        String msg = new String(bytes);
        try {
            if(StringUtils.isNotBlank(msg)){
                JSONObject jsonObject = JSON.parseObject(msg);
                System.out.println(msg);
                boolean idExist = jedis.exists(jsonObject.getString("messageId"));
                if(idExist){
                    System.out.println("已消费过，不再消费");
                }else{
                    jedis.set(String.valueOf(jsonObject.getString("messageId")), "");
                    System.out.println("第一次消费 MSG");
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

/**
 * public void basicNack(long deliveryTag, boolean multiple, boolean requeue)：告诉服务器这个消息我拒绝接收，basicNack可以一次性拒绝多个消息。
   deliveryTag: 表示该消息的index（long类型的数字）；
   multiple: 是否批量(true：将一次性拒绝所有小于deliveryTag的消息)；
   requeue：指定被拒绝的消息是否重新回到队列；
 */
