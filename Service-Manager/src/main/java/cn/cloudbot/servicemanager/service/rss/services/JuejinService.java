package cn.cloudbot.servicemanager.service.rss.services;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.controller.ChannelController;
import cn.cloudbot.servicemanager.service.rss.pojo.ChannelItem;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author: Hitoka
 * @since: 2019-01-02
 **/

@Data
@Component
public class JuejinService extends Servicer<RobotSendMessage> {
    private static Logger logger = Logger.getLogger(JuejinService.class.getName());

    @Autowired
    private ChannelController channelController;

    @Autowired
    private RedisTemplate redisTemplate;

    private RobotSendMessage message;

    private RobotSendMessageSegment[] receivedMsg;

    private RobotRecvMessage sendMsg;

    private String type;

    public Boolean isSentToMe() {
        // 默认第一段消息是命令
        this.receivedMsg =  this.message.getMessage();
        String cmd = this.receivedMsg[0].getData().getText();
        if (cmd.equals("掘金前端")) {
            this.type = "frontend";
            return true;
        }
        else if (cmd.equals("掘金安卓")) {
            this.type = "android";
            return true;
        }
        else if (cmd.equals("掘金苹果")) {
            this.type = "iOS";
            return true;
        }
        else if (cmd.equals("掘金设计")) {
            this.type = "design";
            return true;
        }
        else if (cmd.equals("掘金后端")) {
            this.type = "backend";
            return true;
        }
        return false;
    }

    public void sendBack() {
        // 初始化要回复的消息
        this.sendMsg.setGroup_id(this.message.getGroup_id());
        this.sendMsg.setPlatform(this.message.getPlatform());
        this.sendMsg.setMessage(this.message.getMessage()[0].getData().getText());
        // 获取 rss
        Rss rss = channelController.getJuejinByType(type);
        StringBuilder hot = new StringBuilder();
        ArrayList<ChannelItem> items = rss.getChannel().getItems();
        for (int i = 0; i < 5; i++) {
            hot.append(items.get(i).getTitle() + '\n');
        }
        hot.append("查看更多->https://juejin.im/welcome/" + type);
        sendMsg.setMessage(hot.toString());
        logger.info("[send] juejin service sent " + sendMsg);
        sendProcessedDataBack(sendMsg);
    }


    @Override
    public String serviceName() {
        return "juejin";
    }

    @Override
    public boolean if_accept(RobotSendMessage data) {
        logger.info("[Accept] juejin service accepted the message.");
        return true;
    }

    @Override
    public void running_logic() throws InterruptedException {
        this.message = this.get_data();
        if (isSentToMe()) {
            sendBack();
        }
    }
}
