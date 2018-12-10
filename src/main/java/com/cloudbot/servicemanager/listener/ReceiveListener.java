package com.cloudbot.servicemanager.listener;

import com.cloudbot.servicemanager.pojo.receive.message.ReceiveMessage;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

/**
 * 监听从 botManager 发来的消息
 * @author: Chen Yulei
 * @since: 2018-12-10
 **/
@EnableBinding(Sink.class)
public class ReceiveListener {

    @StreamListener(Sink.INPUT)
    public void receive(Message message) { //Message<ReceiveMessage>
        System.out.print("收到消息：" + message);
    }

    // @Header(name="contentType") Object header
    // @Headers Map<String,Object> header


    /* 发送：

    @EnableBinding(Source.class)
    public class Producer {
        @Autowired
        @Output(Source.OUTPUT)
        private MessageChannel channel;

        public void send() {
            channel.send(MessageBuilder.withPayload("22222222222" + UUID.randomUUID().toString()).build());
        }

    ---------------------
    作者：liaoyuecai
    来源：CSDN
    原文：https://blog.csdn.net/liaoyuecai/article/details/79970126
    版权声明：本文为博主原创文章，转载请附上博文链接！

     */
}
