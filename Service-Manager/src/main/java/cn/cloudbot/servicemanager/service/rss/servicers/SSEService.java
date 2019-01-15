package cn.cloudbot.servicemanager.service.rss.servicers;

import cn.cloudbot.common.Message.BotMessage.MessageSegmentType;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.common.Message2.RobotSendMessage2;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.pojo.ChannelItem;
import cn.cloudbot.servicemanager.service.rss.service.ChannelService;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import cn.cloudbot.servicemanager.service.rss.service.RedisRssService;
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
@Component("sse")
public class SSEService extends Servicer<RobotSendMessage2> {

    @Autowired
    private ChannelService channelController;

    @Autowired
    private RedisRssService redisRssService;

    private RobotSendMessage message;

    public void timer_run() throws InterruptedException {
        Rss rss = channelController.getTJUSSExwdt();
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
        return "sse";
    }

    @Override
    public boolean if_accept(RobotSendMessage2 data) {
        // 是否被AT

        boolean ated = false;
        boolean name_called = false;
        for (RobotSendMessageSegment segment:
                data.getRobotSendMessage().getMessage()) {
            if (segment.getType().equals(MessageSegmentType.AT)) {
                ated = true;
            }

            if (segment.getType().equals(MessageSegmentType.TEXT) && segment.getData().getText().contains(serviceName())) {
                name_called = true;
            }
        }
        return ated && name_called;

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
        }, 10000, 60000);

        while (true) {
            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage(); // 阻塞直到收到消息

            Rss rss = redisRssService.getRssByField(serviceName());

            RobotRecvMessage robotRecvMessage = new RobotRecvMessage();

            robotRecvMessage.setMessage("学院网最新通知："+ rss.getChannel().getItems().get(0).getTitle() +
                    "\n点击查看详情->" + rss.getChannel().getItems().get(0).getLink());

            sendProcessedDataSingle(robotRecvMessage, message2);
        }
    }

}
