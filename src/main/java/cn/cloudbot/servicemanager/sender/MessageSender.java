package cn.cloudbot.servicemanager.sender;

import cn.cloudbot.servicemanager.pojo.message.send.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author: Chen Yulei
 * @since: 2018-12-11
 **/
@EnableBinding(Source.class)
public class MessageSender {

    //消息源
    @Autowired
    private Source source;

    @Output(Source.OUTPUT)
    public void send(SendMessage sendMessage) {
        if (sendMessage == null) {
            throw new RuntimeException("sendMessage is null");
        }
        //源->通道->发送
        source.output().send(MessageBuilder.withPayload(sendMessage).build());
    }

}
