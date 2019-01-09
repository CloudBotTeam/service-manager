package cn.cloudbot.servicemanager.listener;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MessageListener2 {
    String INPUT = "BotMessage2";

    @Input(MessageListener2.INPUT)
    SubscribableChannel input();
}
