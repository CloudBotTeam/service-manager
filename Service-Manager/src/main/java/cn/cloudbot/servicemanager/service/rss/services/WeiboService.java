package cn.cloudbot.servicemanager.service.rss.services;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.controller.ChannelController;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/** 当收到的消息的第一段是「微博」时由此服务回复
 * @author: Hitoka
 * @since: 2019-01-02
 **/

@Data
@Component("weibo-lin")
public class WeiboService extends Servicer<RobotSendMessage> {
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
        if (this.receivedMsg[0].getData().getText().equals("微博")) {
            // 初始化要回复的消息
            this.sendMsg.setGroup_id(this.message.getGroup_id());
            this.sendMsg.setPlatform(this.message.getPlatform());
            this.sendMsg.setMessage(this.message.getMessage()[0].getData().getText());
            return true;
        }
        return false;
    }

    public void sendBack() {
        Rss rss = channelController.getWeiboByUserId("1195354434");
        sendMsg.setMessage("林俊杰最新微博：" + rss.getChannel().getItems().get(0).getTitle() +
                "\n点击查看详情->" + rss.getChannel().getItems().get(0).getLink());
        logger.info("[send] weibo service sent " + sendMsg);
        sendProcessedDataBack(sendMsg);
    }

    // 每 10s 请求一次林俊杰的微博
    public void autoRss() {
        while (true) {
            logger.info("[request] weibo requests");
            Rss rss = channelController.getTJUSSExwdt();
            Rss redisRss = (Rss) redisTemplate.opsForValue().get("weibo");
            if (redisRss == null) {
                redisTemplate.opsForValue().set("weibo", rss);
            }
            else {
                if (rss.getChannel().getItems().get(0).getPubDate() == redisRss.getChannel().getItems().get(0).getPubDate()) {
                    logger.info("No weibo update.");
                }
                else {
                    redisTemplate.opsForValue().set("weibo", rss);
                    sendMsg.setMessage("林俊杰更新微博啦：" + rss.getChannel().getItems().get(0).getTitle() +
                            "\n点击查看详情->" + rss.getChannel().getItems().get(0).getLink());
                    sendProcessedDataBack(sendMsg);
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String serviceName() {
        return "weibo-lin";
    }

    @Override
    public boolean if_accept(RobotSendMessage data) {
        logger.info("[Accept] weibo service accepted the message.");
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
