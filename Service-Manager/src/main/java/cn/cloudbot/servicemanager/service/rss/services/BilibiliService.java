package cn.cloudbot.servicemanager.service.rss.services;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.controller.ChannelController;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.logging.Logger;

/**
 * @author: Hitoka
 * @since: 2019-01-02
 **/
public class BilibiliService extends Servicer<RobotSendMessage> {
    private static Logger logger = Logger.getLogger(BilibiliService.class.getName());

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
        if (this.receivedMsg[0].getData().getText().equals("哔哩哔哩")) {
            // 初始化要回复的消息
            this.sendMsg.setGroup_id(this.message.getGroup_id());
            this.sendMsg.setPlatform(this.message.getPlatform());
            this.sendMsg.setMessage(this.message.getMessage()[0].getData().getText());
            return true;
        }
        return false;
    }

    public void sendBack() {
        Rss rss = channelController.getBilibiliByUserId("11357018");
        sendMsg.setMessage("AnimeTamashii最新视频："+ rss.getChannel().getItems().get(0).getTitle() +
                "\n点击查看详情->" + rss.getChannel().getItems().get(0).getLink());
        logger.info("[send] bilibili service sent " + sendMsg);
        sendProcessedDataBack(sendMsg);
    }

    // 每 30s 请求一次
    public void autoRss() {
        while (true) {
            logger.info("[request] bilibili requests");
            Rss rss = channelController.getTJUSSExwdt();
            Rss redisRss = (Rss) redisTemplate.opsForValue().get("bilibili");
            if (redisRss == null) {
                redisTemplate.opsForValue().set("bilibili", rss);
            }
            else {
                if (rss.getChannel().getItems().get(0).getPubDate() == redisRss.getChannel().getItems().get(0).getPubDate()) {
                    logger.info("No bilibili video update.");
                }
                else {
                    redisTemplate.opsForValue().set("bilibili", rss);
                    sendMsg.setMessage("AnimeTamashii发新视频啦：" + rss.getChannel().getItems().get(0).getTitle() +
                            "\n点击查看->" + rss.getChannel().getItems().get(0).getLink());
                    sendProcessedDataBack(sendMsg);
                }
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String serviceName() {
        return "bilibili";
    }

    @Override
    public boolean if_accept(RobotSendMessage data) {
        logger.info("[Accept] bilibili service accepted the message.");
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
