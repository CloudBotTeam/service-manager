package cn.cloudbot.servicemanager.listener;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import java.util.logging.Logger;
//import org.springframework.messaging.Message;

/**
 * 监听从 botManager 发来的消息
 * @author: Chen Yulei
 * @since: 2018-12-10
 **/
@EnableBinding(Sink.class)
public class ReceiveListener {

    private Logger logger = Logger.getLogger(ReceiveListener.class.getName());

    @StreamListener(Sink.INPUT)
    //？参数不用 Message message 是可以的吗？
    public void receive(RobotSendMessage receiveMessage) { //Message<ReceiveMessage>

        logger.info("收到消息：" + receiveMessage.toString());


        /* findServiceByGroupId()

        // for() {
            if(service[i].KEY == receiveMessage.messageSegments[0].messageData) {
                service[i].run(receiveMessage.id);
            }
        }
        */
        //收到消息发给每一个服务
        //服务去判断是否是自己的消息 然后调用sender发送消息

        //也可以在这里判断是否是某个服务的消息
    }

    // @Header(name="contentType") Object header
    // @Headers Map<String,Object> header

}
