package com.gmall.service;


import com.gmall.entity.MessagePushConfirm;

import java.util.List;

public interface IMessageService  {

    void insertMessage(MessagePushConfirm message);

    List<MessagePushConfirm> getMessageList(int status);

    String sendLoginMergeCartMessageByRabbitMQ(String messageData);

    void updateMessage(MessagePushConfirm messagePushConfirm);
}
