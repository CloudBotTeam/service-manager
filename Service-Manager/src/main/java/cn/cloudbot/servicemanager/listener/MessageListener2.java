package cn.cloudbot.servicemanager.listener;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MessageListener2 {
    String INPUT = "BotManagerMessage2";

    @Input(Sink.INPUT)
    SubscribableChannel input();
}
