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
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * @author: Xuan
 * @since: 2019-05-27
 **/

//@Data
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
            sendBroadcast(rss.getChannel().getItems().toString());
        }
        else {
            if(rss.equals(redisRss)) {
                return;

            } else {
                // update cache
                redisRssService.setRssWithField(serviceName(), rss);
//                sendBroadcast(rss.getChannel().getItems().toString());
            }
        }
    }

    @Override
    public String serviceName() {
        return "news";
    }

    @Override
    public boolean if_accept(RobotSendMessage2 data) {
        // 是否调用当前news服务

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

    public  void test(){
        Calendar calendar = Calendar.getInstance();

    //19：00定时发送
        calendar.set(Calendar.DAY_OF_MONTH,17);
        calendar.set(Calendar.MONTH, 5);
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 20);
        calendar.set(Calendar.SECOND, 1);

        Date time = calendar.getTime();
        Timer timer = new Timer();
//        timer.schedule(new RemindTask(), time, 60 * 1000);
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
//            test();
            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage(); // 阻塞直到收到消息

//            Rss rss = redisRssService.getRssByField(serviceName());
            Rss rss = channelController.getNews();

            RobotRecvMessage robotRecvMessage = new RobotRecvMessage();

            StringBuilder news = new StringBuilder();
            ArrayList<ChannelItem> items = rss.getChannel().getItems();

            news.append("小报童来了~📰 热点新闻请您查收！\n");
            //返回十条央视最新新闻
            for (int i = 0; i < 3; i++) {
                news.append("💡 " + items.get(i).getTitle() + '\n');
            }
            news.append("戳这里可以阅读更多新闻👉http://news.cctv.com/world");
//            system.out(news)

            robotRecvMessage.setMessage(news.toString());

            sendProcessedDataSingle(robotRecvMessage, message2);
        }
    }
}

