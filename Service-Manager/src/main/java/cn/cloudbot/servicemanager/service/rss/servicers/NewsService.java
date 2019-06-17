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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * @author: Xuan
 * @since: 2019-05-27
 **/

@Data
@Component("news")
public class NewsService extends Servicer<RobotSendMessage2> {
    private static Logger logger = Logger.getLogger(NewsService.class.getName());

    @Autowired
    private ChannelService channelController;

    @Autowired
    private RedisRssService redisRssService;

    private RobotSendMessage message;

    public void timer_run() throws InterruptedException {
        logger.info("[request] news requests");

        Rss rss = channelController.getNews();
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
            }
        }
    }

    @Override
    public String serviceName() {
        return "news";
    }

    @Override
    public boolean if_accept(RobotSendMessage2 data) {
        // 是否被AT

//        boolean ated = false;
        boolean name_called = false;
        for (RobotSendMessageSegment segment:
                data.getRobotSendMessage().getMessage()) {
//            if (segment.getType().equals(MessageSegmentType.AT)) {
//                ated = true;
//            }

            if (segment.getType().equals(MessageSegmentType.TEXT) && segment.getData().getText().contains("新闻")) {
                name_called = true;
            }
        }
        logger.info("[Accept] news service accepted the message.");

        return name_called;

    }


    @Override
    public void running_logic() throws InterruptedException {
        // 自动推送子线程
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run(){
//                try {
//                    timer_run();
//                } catch (InterruptedException e) {
//                }
//
//            }
//        }, 10000, 60000);

        while (true) {
            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage(); // 阻塞直到收到消息

//            Rss rss = redisRssService.getRssByField(serviceName());
            Rss rss = channelController.getNews();

            RobotRecvMessage robotRecvMessage = new RobotRecvMessage();

            StringBuilder news = new StringBuilder();
            ArrayList<ChannelItem> items = rss.getChannel().getItems();

            news.append("没想到你还是一个关注新闻的人😲！现在最新的新闻📰有：");
            //返回十条央视最新新闻
            for (int i = 0; i < 3; i++) {
                news.append(items.get(i).getTitle() + '，');
            }
            news.append("戳这里可以阅读更多新闻👉http://news.cctv.com/world");
//            system.out(news)
            robotRecvMessage.setMessage(news.toString());

            sendProcessedDataSingle(robotRecvMessage, message2);

//            sendBroadcast(news.toString());

        }
    }

}
