package cn.cloudbot.servicemanager.listener;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface Sink {
    String INPUT = "BotManagerMessage";

    @Input(Sink.INPUT)
    SubscribableChannel input();
}
