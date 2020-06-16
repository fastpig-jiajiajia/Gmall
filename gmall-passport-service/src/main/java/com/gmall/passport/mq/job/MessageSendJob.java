package com.gmall.passport.mq.job;


import com.gmall.GmallConstant;
import com.gmall.entity.MessagePushConfirm;
import com.gmall.passport.service.impl.MessageServiceImpl;
import com.gmall.service.IMessageService;
import com.gmall.util.SpringBeanUtil;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageSendJob {

    private static Logger log = Logger.getLogger(MessageSendJob.class);
    //  0/10 * * * * ?
    //  0 0/2 * * * ?
    @Scheduled(cron = "0/10 * * * * ?")  // 每 10 秒执行一次
    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.READ_COMMITTED)
    public void sendMergeCartMessage() {
        IMessageService messageService = (MessageServiceImpl) SpringBeanUtil.getBean("messageServiceImpl");

        List<MessagePushConfirm> messageList = new ArrayList<>();
        messageList = messageService.getMessageList(GmallConstant.ZERO_INT);
        log.info(new StringBuilder("sendMergeCartMessageJob ").append((CollectionUtils.isEmpty(messageList) ? " 0 条记录" : messageList.size() + " 条记录")).toString());

        // 不需要判断消息是否发送过，消费端进行幂等性检查
        messageList.stream().map(message -> {
            try{
                String flag = messageService.sendLoginMergeCartMessageByRabbitMQ(message.getMessage());
                if(GmallConstant.SUCCESS.equals(flag)){
                    message.setStatus(GmallConstant.ONE_INT);
                    messageService.updateMessage(message);
                }
            }catch (Exception e){
                log.error(e);
                log.error("sendMergeCartMessageJob error");
            }
            return message;
        }).count();
    }
}
