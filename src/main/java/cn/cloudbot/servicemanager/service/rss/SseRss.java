package cn.cloudbot.servicemanager.service.rss;

import cn.cloudbot.servicemanager.listener.MessageSendBacker;
import cn.cloudbot.servicemanager.pojo.message.send.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author: Chen Yulei
 * @since: 2018-12-11
 **/

//public class SseRss extends RSS {
//
//    private final String KEY = "软院通知";
//    private SendMessage sendMessage;
//
//    @Autowired
//    private MessageSendBacker messageSender;
//
//    @Override
//    public void run(String groupId) {
//        //构建sendMessage
//        sendMessage = new SendMessage();
//        sendMessage.setGroup_id(groupId);
//        sendMessage.setReply("这是一条测试通知");
//        sendMessage.setAuto_escape(false);
//        //发送message
//        messageSender.sendData().send(MessageBuilder.withPayload(sendMessage).build());
//    }
//}
