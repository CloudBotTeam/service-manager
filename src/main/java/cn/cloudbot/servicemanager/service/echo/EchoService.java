package cn.cloudbot.servicemanager.service.echo;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.servicemanager.listener.Sender;
import cn.cloudbot.servicemanager.pojo.message.receive.ReceiveMessage;
import cn.cloudbot.servicemanager.service.Servicer;
import org.springframework.messaging.handler.annotation.SendTo;

public class EchoService extends Servicer<RobotSendMessage>{
    public EchoService(String service_name) {
        super(service_name);
    }

    @SendTo(Sender.OUTPUT_CHANNEL)
    public RobotRecvMessage sendEcho(ReceiveMessage message) {
        return null;
    }

    @Override
    public boolean if_accept(RobotSendMessage data) {
        return true;
    }

    @Override
    public void running_logic() throws InterruptedException {
        while (true) {
            RobotSendMessage data = this.get_data();
            sendEcho(data);
        }
    }
}
