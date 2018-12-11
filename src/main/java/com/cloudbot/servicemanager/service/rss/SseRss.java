package com.cloudbot.servicemanager.service.rss;

import com.cloudbot.servicemanager.pojo.message.send.SendMessage;
import com.cloudbot.servicemanager.sender.MessageSender;

/**
 * @author: Chen Yulei
 * @since: 2018-12-11
 **/

public class SseRss extends RSS {

    private final String KEY = "软院通知";
    private SendMessage sendMessage;
    private MessageSender messageSender;

    @Override
    public void run(String groupId) {
        //构建sendMessage
        sendMessage = new SendMessage();
        sendMessage.setGroup_id(groupId);
        sendMessage.setReply("这是一条测试通知");
        sendMessage.setAuto_escape(false);
        //发送message
        messageSender.send(sendMessage);
    }
}
