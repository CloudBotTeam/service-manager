package cn.cloudbot.servicemanager.listener;

import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Component
public class BackSender {
    @Autowired
    private MessageSendBacker sendBacker;

    public void sendProcessedDataBack(RobotRecvMessage message) {

        sendBacker.sendData().send(MessageBuilder.withPayload(message).build());
    }

}
