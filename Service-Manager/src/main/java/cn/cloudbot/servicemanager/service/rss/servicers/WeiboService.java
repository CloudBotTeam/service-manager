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
 * @author: Hitoka
 * @since: 2019-01-02
 **/

@Data
@Component("weibo")
public class WeiboService extends Servicer<RobotSendMessage2> {
    private static Logger logger = Logger.getLogger(NewsService.class.getName());

    @Autowired
    private ChannelService channelController;

    @Autowired
    private RedisRssService redisRssService;

    private RobotSendMessage message;

    public void timer_run() throws InterruptedException {
//        Rss rss = channelController.getWeiboByUserId();
        Rss rss = channelController.getWeiboByUserId("2883790292");
        Rss redisRss = redisRssService.getRssByField(serviceName());
        if (redisRss == null) {
            redisRssService.setRssWithField(serviceName(), rss);
        }
        else {
//            if(rss.equals(redisRss)) {
            if (rss.getChannel().getItems().get(0).getTitle().equals(redisRss.getChannel().getItems().get(0).getTitle())) {
                // data cached in redis is not out date
//                logger.info("[ == 微博博主 == 无 == ] 检测到没有新微博");
//                logger.info("rss ==>" + rss.getChannel().getItems().get(0).getTitle());
//                logger.info("redis ==>" + redisRss.getChannel().getItems().get(0).getTitle());
                return;
            } else {
                // update cache
                redisRssService.setRssWithField(serviceName(), rss);
//                RobotRecvMessage robotRecvMessage = new RobotRecvMessage();

                logger.info("[ == 微博博主 == ] 检测到新微博");

                // StringBuilder hot = new StringBuilder();
                // hot.append("新鲜出炉的微博热搜来咯！前三的话题是：");
                // ArrayList<ChannelItem> items = rss.getChannel().getItems();
                // for (int i = 1; i < 4; i++) {
                //     hot.append(items.get(i).getTitle() + '，');
                // }
                // hot.append("(￣▽￣)\" 大家都好八卦啊~\n戳这里可以看更多八卦哦➡️https://s.weibo.com/top/summary?cate=realtimehot");

//                robotRecvMessage.setMessage("叮咚！你订阅的博主发微博了！😀Ta说：" + rss.getChannel().getItems().get(0).getTitle() +
//                        "\n快戳这里第一时间抢沙发！👉" + rss.getChannel().getItems().get(0).getLink());

//                sendProcessedDataSingle(robotRecvMessage, message2);
                sendBroadcast("叮咚！你订阅的博主发微博了！😀Ta说：" + rss.getChannel().getItems().get(0).getTitle() +
                        "\n快戳这里第一时间抢沙发！👉" + rss.getChannel().getItems().get(0).getLink());
            }
        }
    }

    @Override
    public String serviceName() {
        return "weibo";
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

            if (segment.getType().equals(MessageSegmentType.TEXT) )
                if (segment.getData().getText().contains("最新微博") ||
                        segment.getData().getText().contains("最新一条微博")) {
                name_called = true;
            }
        }
        return name_called;

    }


    @Override
    public void running_logic() throws InterruptedException {
        // 自动推送子线程
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                try {
                    timer_run();
                } catch (InterruptedException e) {
                }

            }
        }, 10000, 10000);
        // 10000, 60000

        while (true) {
            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage(); // 阻塞直到收到消息

//            Rss rss = redisRssService.getRssByField(serviceName());
            Rss rss = channelController.getWeiboByUserId("2883790292");

            RobotRecvMessage robotRecvMessage = new RobotRecvMessage();

            // StringBuilder hot = new StringBuilder();
            // hot.append("新鲜出炉的微博热搜来咯！前三的话题是：");
            // ArrayList<ChannelItem> items = rss.getChannel().getItems();
            // for (int i = 1; i < 4; i++) {
            //     hot.append(items.get(i).getTitle() + '，');
            // }
            // hot.append("(￣▽￣)\" 大家都好八卦啊~\n戳这里可以看更多八卦哦➡️https://s.weibo.com/top/summary?cate=realtimehot");

            robotRecvMessage.setMessage("叮咚！你订阅的博主发微博了！😀Ta说：" + rss.getChannel().getItems().get(0).getTitle() +
                    "\n快戳这里第一时间抢沙发！👉" + rss.getChannel().getItems().get(0).getLink());

            sendProcessedDataSingle(robotRecvMessage, message2);
        }
    }

}
