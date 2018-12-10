package com.cloudbot.servicemanager.listener;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface Sink {
    String INPUT = "serviceInput";

    @Input(Sink.INPUT)
    SubscribableChannel input();
}
