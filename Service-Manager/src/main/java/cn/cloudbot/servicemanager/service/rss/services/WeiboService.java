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

/**
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

    // 每 10s 请求一次林俊杰的微博
    public class AutoWeibo implements Runnable {
        @Override
        public void run() {
            while (true) {
                logger.info("[request] weibo-lin requests");
                Rss rss = channelController.getTJUSSExwdt();
                Rss redisRss = (Rss) redisTemplate.opsForValue().get("weibo-lin");
                if (redisRss == null) {
                    redisTemplate.opsForValue().set("weibo-lin", rss);
                }
                else {
                    if (rss.getChannel().getItems().get(0).getPubDate() == redisRss.getChannel().getItems().get(0).getPubDate()) {
                        logger.info("No weibo-lin update.");
                    }
                    else {
                        redisTemplate.opsForValue().set("weibo-lin", rss);
                        String broadcastMsg = "林俊杰更新微博啦：" + rss.getChannel().getItems().get(0).getTitle() +
                                "\n点击查看详情->" + rss.getChannel().getItems().get(0).getLink();
                        sendBroadcast(broadcastMsg);
                    }
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Boolean isSentToMe() {
        // 默认第一段消息是命令
        this.receivedMsg =  this.message.getMessage();
        if (this.receivedMsg[0].getData().getText().equals("微博林俊杰")) {
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


    @Override
    public String serviceName() {
        return "weibo-lin";
    }

    @Override
    public boolean if_accept(RobotSendMessage data) {
        // 每条都收
        logger.info("[Accept] weibo service accepted the message.");
        return true;
    }

    @Override
    public void running_logic() throws InterruptedException {
        Thread autoRss = new Thread(new AutoWeibo());
        autoRss.setDaemon(true);
        autoRss.start();
        while (true) {
            this.message = this.get_data();
            if (isSentToMe()) {
                sendBack();
            }
        }
    }
}
