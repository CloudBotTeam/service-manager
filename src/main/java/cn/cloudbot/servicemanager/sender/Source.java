package cn.cloudbot.servicemanager.sender;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface Source {

    String OUTPUT = "serviceOutput";

    @Output(Source.OUTPUT)
    MessageChannel output();
}
