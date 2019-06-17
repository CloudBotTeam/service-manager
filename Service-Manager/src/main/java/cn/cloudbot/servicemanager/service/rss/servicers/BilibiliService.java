package cn.cloudbot.servicemanager.service.rss.servicers;

import cn.cloudbot.common.Message.BotMessage.MessageSegmentType;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.common.Message2.RobotRecvMessage2;
import cn.cloudbot.common.Message2.RobotSendMessage2;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.service.ChannelService;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import cn.cloudbot.servicemanager.service.rss.service.RedisRssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * @author: Hitoka
 * @since: 2019-01-02
 **/
@Component("up")
public class BilibiliService extends Servicer<RobotSendMessage2> {
    private static Logger logger = Logger.getLogger(BilibiliService.class.getName());

    @Autowired
    private ChannelService channelController;

    @Autowired
    private RedisRssService redisRssService;

    private RobotSendMessage message;

    public void timer_run() throws InterruptedException {
        logger.info("[request] up requests");
        Rss rss = channelController.getBiliToday();
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
        return "up";
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

            if (segment.getType().equals(MessageSegmentType.TEXT)) {
                 if (segment.getData().getText().contains("up 主") ||
                         segment.getData().getText().contains("up主") ||
                         segment.getData().getText().contains("阿婆主") ||
                         segment.getData().getText().contains("b 站") ||
                         segment.getData().getText().contains("b站") ||
                         segment.getData().getText().contains("B 站") ||
                         segment.getData().getText().contains("B站")) {
                     name_called = true;
                 }
            }
        }
        logger.info("[Accept] anitamashii service accepted the message.");
        return name_called;

    }


    @Override
    public void running_logic() throws InterruptedException {
        // 自动推送子线程
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run(){
//                try {
//                    logger.info("Timer TTask called.");
//                    timer_run();
//                } catch (InterruptedException e) {
//                    logger.info("Timer meets interupts");
//                }
//
//            }
//        }, 10000, 60000);

        while (true) {
            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage(); // 阻塞直到收到消息

//            Rss rss = redisRssService.getRssByField(serviceName());
            Rss rss = channelController.getBilibiliByUserId("7584632");


            RobotRecvMessage robotRecvMessage = new RobotRecvMessage();

            robotRecvMessage.setMessage("你关注的up主「哔哩哔哩纪录片」的最新视频是："+ rss.getChannel().getItems().get(0).getTitle() +
                    "\n️戳这里看视频👉" + rss.getChannel().getItems().get(0).getLink());

            sendProcessedDataSingle(robotRecvMessage, message2);
        }
    }
}
