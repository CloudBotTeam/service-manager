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
@Component("movie")
public class MovieService extends Servicer<RobotSendMessage> {
    private static Logger logger = Logger.getLogger(MovieService.class.getName());

    @Autowired
    private ChannelController channelController;

    private RobotSendMessage message;

    private RobotSendMessageSegment[] receivedMsg;

    private RobotRecvMessage sendMsg;

    // auto rss every day
    public class AutoMovie implements Runnable {
        @Override
        public void run() {
            while (true) {
                logger.info("[request] movie requests");
                Rss rss = channelController.getMovie();
                StringBuilder hot = new StringBuilder();
                ArrayList<ChannelItem> items = rss.getChannel().getItems();
                for (int i = 0; i < 10; i++) {
                    hot.append(items.get(i).getTitle() + '\n');
                }
                hot.append("查看更多->https://movie.douban.com/cinema/nowplaying");
//                sendMsg.setMessage(hot.toString());
                logger.info("[send] movie service sent " + hot.toString());
//                sendProcessedDataBack(sendMsg);
                sendBroadcast(hot.toString());
                try {
                    Thread.sleep(86400000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Boolean isSentToMe() {
        // 默认第一段消息是命令
        this.receivedMsg =  this.message.getMessage();
        if (this.receivedMsg[0].getData().getText().equals("电影")) {
            // 初始化要回复的消息
            this.sendMsg.setGroup_id(this.message.getGroup_id());
            this.sendMsg.setPlatform(this.message.getPlatform());
            this.sendMsg.setMessage(this.message.getMessage()[0].getData().getText());
            return true;
        }
        return false;
    }

    public void sendBack() {
        Rss rss = channelController.getMovie();
        StringBuilder hot = new StringBuilder();
        ArrayList<ChannelItem> items = rss.getChannel().getItems();
        for (int i = 0; i < 10; i++) {
            hot.append(items.get(i).getTitle() + '\n');
        }
        hot.append("查看更多->https://movie.douban.com/cinema/nowplaying");
        sendMsg.setMessage(hot.toString());
        logger.info("[send] movie service sent " + sendMsg);
        sendProcessedDataBack(sendMsg);
    }

    @Override
    public String serviceName() {
        return "movie";
    }

    @Override
    public boolean if_accept(RobotSendMessage data) {
        // 每条都收
        logger.info("[Accept] movie service accepted the message.");
        return false;
    }

    @Override
    public void running_logic() throws InterruptedException {
        Thread autoRss = new Thread(new AutoMovie());
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
