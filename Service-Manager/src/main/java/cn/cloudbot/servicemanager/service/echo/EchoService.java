package cn.cloudbot.servicemanager.service.echo;

import cn.cloudbot.common.Message.BotMessage.MessageSegmentType;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.common.Message2.RobotRecvMessage2;
import cn.cloudbot.common.Message2.RobotSendMessage2;
import cn.cloudbot.servicemanager.service.Servicer;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component("echo")
public class EchoService extends Servicer<RobotSendMessage2>{
    private static Logger logger = Logger.getLogger(EchoService.class.getName());


    public void sendEcho(RobotSendMessage message) {
        logger.info("Send Echo");

        RobotRecvMessage resp = new RobotRecvMessage();
        resp.setGroup_id(message.getGroup_id());
        resp.setPlatform(message.getPlatform());
        resp.setMessage(message.getMessage()[0].getData().getText());
        logger.info("send " + resp + " back");
        sendProcessedDataBack(resp);
    }

    @Override
    public String serviceName() {
        return "echo";
    }

    @Override
    public boolean if_accept(RobotSendMessage2 data) {
        return true;
    }


    private String lastMessage;

    @Override
    public void running_logic() throws InterruptedException {
        while (true) {
            RobotSendMessage2 data = this.get_data();
            logger.info("Get data " + data);
            RobotRecvMessage2 message2 = new RobotRecvMessage2();
            message2.setRobot_ip(data.getRobotIp());

            StringBuilder builder = new StringBuilder();

            for (RobotSendMessageSegment segment:
                data.getRobotSendMessage().getMessage()) {
                logger.info("segment " + segment);
                if (segment.getType().equals(MessageSegmentType.TEXT)) {
                    builder.append(segment.getData().getText());
                }
            }
            String currentString = builder.toString();
            if (currentString.equals(lastMessage)) {
                continue;
            }
            lastMessage = currentString;
            message2.setMessage(currentString);
            message2.setGroup_id(data.getRobotSendMessage().getGroup_id());
            sendProcessedDataSingle(message2);
        }
    }
}
