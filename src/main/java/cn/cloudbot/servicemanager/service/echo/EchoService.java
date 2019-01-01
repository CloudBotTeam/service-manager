package cn.cloudbot.servicemanager.service.echo;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.servicemanager.service.Servicer;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component("echo")
public class EchoService extends Servicer<RobotSendMessage>{
    private static Logger logger = Logger.getLogger(EchoService.class.getName());


    public void sendEcho(RobotSendMessage message) {
        logger.info("Send Echo");

        RobotRecvMessage resp = new RobotRecvMessage();
        resp.setGroup_id(message.getGroup_id());
        resp.setPlatform(message.getPlatform());
        resp.setMessage(message.getMessage()[0].getData().getText());
        sendProcessedDataBack(resp);
    }

    @Override
    public String serviceName() {
        return "echo";
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
