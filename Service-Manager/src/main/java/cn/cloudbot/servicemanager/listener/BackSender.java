package cn.cloudbot.servicemanager.listener;

import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.common.Message2.RobotRecvMessage2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;


@Component
public class BackSender {
    private Logger logger = Logger.getLogger(BackSender.class.getName());

    @Autowired
    private MessageSendBacker2 sendBacker2;

    public void sendProcessedDataBack(RobotRecvMessage message) {
        logger.info("recv message in deprecated method.");

//        sendBacker.sendData().send(MessageBuilder.withPayload(message).build());
    }

    public void sendProcessedDataBack2(RobotRecvMessage2 message) {
        sendBacker2.sendData().send(MessageBuilder.withPayload(message).build());
    }

    public void sendBroadcastMessage(RobotRecvMessage2 message2) {
        message2.setType("all");
    }

}
