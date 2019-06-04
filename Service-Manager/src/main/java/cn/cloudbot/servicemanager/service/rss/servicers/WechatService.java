package cn.cloudbot.servicemanager.service.rss.servicers;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.pojo.ChannelItem;
import cn.cloudbot.servicemanager.service.rss.service.ChannelService;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.logging.Logger;

import java.util.HashMap;


@Data
@Component("wechat")
public class WechatService extends Servicer<RobotSendMessage> {

    private static Logger logger = Logger.getLogger(WechatService.class.getName());

    @Autowired
    private ChannelService channelController;

    @Autowired
    private RedisTemplate redisTemplate;

    private RobotSendMessage message;

    private RobotSendMessageSegment[] receivedMsg;

    private RobotRecvMessage sendMsg;

    // 公众号名称
    private String account_name;

    // 公众号及对应的 ID
    private HashMap<String, String> accountID = new HashMap<>();


    public Boolean isSentToMe() {

        // 默认第一段消息是命令
        this.receivedMsg =  this.message.getMessage();
        String cmd = this.receivedMsg[0].getData().getText();
        // 输入格式示例：wechat 机器之心
        if (cmd.substring(0, 6).equals("wechat")) {
            this.account_name = cmd.substring(7);
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
        Rss rss = channelController.getWechatById(accountID.get(account_name));

        StringBuilder articles = new StringBuilder();
        articles.append(account_name + "公众号的最新三篇推送：\n");
        ArrayList<ChannelItem> items = rss.getChannel().getItems();
        for (int i = 0; i < 3; i++) {
            articles.append(items.get(i).getTitle() + '\n');
            articles.append("阅读这篇文章-> " + items.get(i).getLink() + '\n');
        }

        sendMsg.setMessage(articles.toString());
        logger.info("[send] wechat service sent " + sendMsg);
        sendProcessedDataBack(sendMsg);
    }


    @Override
    public String serviceName() {
        return "wechat";
    }

    @Override
    public boolean if_accept(RobotSendMessage data) {
        // 每一条都收
        logger.info("[Accept] wechat service accepted the message.");
        return false;
    }


    @Override
    public void running_logic() throws InterruptedException {

        this.accountID.put("同济大学", "5c276fa3497ff40f7fe4b81d");
        this.accountID.put("机器之心", "5b575dd058e5c4583338dbd3");

        while (true) {
            this.message = this.get_data();
            if (isSentToMe()) {
                sendBack();
            }
        }
    }
}