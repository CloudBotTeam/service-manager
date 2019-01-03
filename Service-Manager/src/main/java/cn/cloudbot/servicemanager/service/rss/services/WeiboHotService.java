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
public class WeiboHotService extends Servicer<RobotSendMessage> {
    private static Logger logger = Logger.getLogger(WeiboService.class.getName());

    @Autowired
    private ChannelController channelController;

    @Autowired
    private RedisTemplate redisTemplate;

    private RobotSendMessage message;

    private RobotSendMessageSegment[] receivedMsg;

    private RobotRecvMessage sendMsg;

    public Boolean isSentToMe() {
        // 默认第一段消息是命令
        this.receivedMsg =  this.message.getMessage();
        if (this.receivedMsg[0].getData().getText().equals("热搜")) {
            // 初始化要回复的消息
            this.sendMsg.setGroup_id(this.message.getGroup_id());
            this.sendMsg.setPlatform(this.message.getPlatform());
            this.sendMsg.setMessage(this.message.getMessage()[0].getData().getText());
            return true;
        }
        return false;
    }

    public void sendBack() {
        Rss rss = channelController.getWeiboHot();
        StringBuilder hot = new StringBuilder();
        ArrayList<ChannelItem> items = rss.getChannel().getItems();
        for (int i = 0; i < 10; i++) {
            hot.append(items.get(i).getTitle() + '\n');
        }
        hot.append("查看更多->https://s.weibo.com/top/summary?cate=realtimehot");
        sendMsg.setMessage(hot.toString());
        logger.info("[send] hot service sent " + sendMsg);
        sendProcessedDataBack(sendMsg);
    }


    @Override
    public String serviceName() {
        return "hot";
    }

    @Override
    public boolean if_accept(RobotSendMessage data) {
        logger.info("[Accept] hot service accepted the message.");
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
