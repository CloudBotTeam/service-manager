package cn.cloudbot.servicemanager.listener;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;


public interface MessageSendBacker2 {
    String OUTPUT_CHANNEL = "ServiceManagerMessage2";
    //    WrappedOutputData 类型的数据被发送
    @Output(OUTPUT_CHANNEL)
    MessageChannel sendData();
}
