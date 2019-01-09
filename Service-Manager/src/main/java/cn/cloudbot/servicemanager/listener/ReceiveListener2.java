package cn.cloudbot.servicemanager.listener;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message2.RobotSendMessage2;
import cn.cloudbot.servicemanager.service.ServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import java.util.logging.Logger;

@EnableBinding(MessageListener2.class)
public class ReceiveListener2 {
    @Autowired
    private ServiceManager serviceManager;

    private Logger logger = Logger.getLogger(ReceiveListener2.class.getName());

    @StreamListener(MessageListener2.INPUT)
    //？参数不用 Message message 是可以的吗？
    public void receive(RobotSendMessage2 receiveMessage) { //Message<ReceiveMessage>

        logger.info("收到消息：" + receiveMessage.toString());

        for (String serv_name:
             receiveMessage.getServices()) {
//            logger.info("Recv");
            logger.info("Send data " + receiveMessage + " to service " + serv_name);
            serviceManager.async_send_to_servicer2(receiveMessage, serv_name);
        }
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
}
