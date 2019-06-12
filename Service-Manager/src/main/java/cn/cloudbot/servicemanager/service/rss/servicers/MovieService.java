package cn.cloudbot.servicemanager.service.rss.servicers;

import cn.cloudbot.common.Message.BotMessage.MessageSegmentType;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.common.Message2.RobotSendMessage2;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.service.ChannelService;
import cn.cloudbot.servicemanager.service.rss.pojo.ChannelItem;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import cn.cloudbot.servicemanager.service.rss.service.RedisRssService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * @author: Hitoka
 * @since: 2019-01-02
 **/

@Data
@Component("movie")
public class MovieService extends Servicer<RobotSendMessage2> {
    private static Logger logger = Logger.getLogger(MovieService.class.getName());

    @Autowired
    private ChannelService channelController;

    @Autowired
    private RedisRssService redisRssService;

    private RobotSendMessage message;

    public void timer_run() throws InterruptedException {
        logger.info("[request] movie requests");
        Rss rss = channelController.getMovie();
        Rss redisRss = redisRssService.getRssByField(serviceName());
        if (redisRss == null) {
            redisRssService.setRssWithField(serviceName(), rss);
        }
        else {
            if(rss.equals(redisRss)) {
                // data cached in redis is not out date
                return;
            } else {
                // update cache
                redisRssService.setRssWithField(serviceName(), rss);
                sendBroadcast(rss.getChannel().getItems().toString());
            }
        }
    }

    @Override
    public String serviceName() {
        return "movie";
    }

    @Override
    public boolean if_accept(RobotSendMessage2 data) {
        // 是否被AT

        boolean name_called = false;
        for (RobotSendMessageSegment segment:
                data.getRobotSendMessage().getMessage()) {
//            if (segment.getType().equals(MessageSegmentType.AT)) {
//                ated = true;
//            }

            if (segment.getType().equals(MessageSegmentType.TEXT) && segment.getData().getText().contains("电影")) {
                name_called = true;
            }
        }
        logger.info("[Accept] movie service accepted the message.");
        return name_called;

    }


    @Override
    public void running_logic() throws InterruptedException {
        // 自动推送子线程
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                try {
                    logger.info("Timer TTask called.");
                    timer_run();
                } catch (InterruptedException e) {
                    logger.info("Timer meets interupts");
                }

            }
        }, 10000, 60000);

        while (true) {
            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage(); // 阻塞直到收到消息

            Rss rss = redisRssService.getRssByField(serviceName());

            RobotRecvMessage robotRecvMessage = new RobotRecvMessage();

            StringBuilder hot = new StringBuilder();
            ArrayList<ChannelItem> items = rss.getChannel().getItems();
            for (int i = 0; i < 10; i++) {
                hot.append(items.get(i).getTitle() + '\n');
            }
            hot.append("查看更多->https://movie.douban.com/cinema/nowplaying");

            robotRecvMessage.setMessage(hot.toString());

            sendProcessedDataSingle(robotRecvMessage, message2);
        }
    }
}
