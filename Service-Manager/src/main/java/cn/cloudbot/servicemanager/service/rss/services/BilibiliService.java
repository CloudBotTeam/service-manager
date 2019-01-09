package cn.cloudbot.servicemanager.service.rss.services;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.common.Message2.RobotSendMessage2;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.controller.ChannelController;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * @author: Hitoka
 * @since: 2019-01-02
 **/
@Component("anitamashii")
public class BilibiliService extends Servicer<RobotSendMessage2> {
    private static Logger logger = Logger.getLogger(BilibiliService.class.getName());

    @Autowired
    private ChannelController channelController;

    @Autowired
    private RedisTemplate redisTemplate;

    private RobotSendMessage message;

    private RobotSendMessageSegment[] receivedMsg;

    private RobotRecvMessage sendMsg;

    // 自动推送子线程
    // 每 30s 请求一次
    public class AutoBilibili implements Runnable{
        @Override
        public void run() {
            while (true) {
                logger.info("[request] anitamashii requests");
                Rss rss = channelController.getBiliToday();
                Rss redisRss = (Rss) redisTemplate.opsForValue().get("anitamashii");
                if (redisRss == null) {
                    redisTemplate.opsForValue().set("anitamashii", rss);
                }
                else {
                    if (rss.getChannel().getItems().get(0).getPubDate() == redisRss.getChannel().getItems().get(0).getPubDate()) {
                        logger.info("No new anitamashii video update.");
                    }
                    else {
                        redisTemplate.opsForValue().set("anitamashii", rss);

                        String broadcastMsg = "AnimeTamashii发新视频啦：" + rss.getChannel().getItems().get(0).getTitle() +
                                "\n点击查看->" + rss.getChannel().getItems().get(0).getLink();
                        sendBroadcast(broadcastMsg);
                    }
                }
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initSendMsg() {
        // 初始化要回复的消息
        this.sendMsg.setGroup_id(this.message.getGroup_id());
        this.sendMsg.setPlatform(this.message.getPlatform());
        this.sendMsg.setMessage(this.message.getMessage()[0].getData().getText());
    }

    public Boolean isSentToMe() {
        // 默认第一段消息是命令
//        this.receivedMsg =  this.message.getMessage();
//        if (this.receivedMsg[0].getData().getText().equals("哔哩哔哩")) {
//            initSendMsg();
//            return true;
//        }
//        return false;
        return true;
    }

    public void sendBack(RobotSendMessage2 sendMessage2) {
        Rss rss = channelController.getBilibiliByUserId("11357018");
        sendMsg.setMessage("AnimeTamashii最新视频："+ rss.getChannel().getItems().get(0).getTitle() +
                "\n点击查看详情->" + rss.getChannel().getItems().get(0).getLink());

        logger.info("[send] anitamashii service sent " + sendMsg);
        sendProcessedDataSingle(sendMsg, sendMessage2);
//        sendProcessedDataBack(sendMsg);
    }

    @Override
    public String serviceName() {
        return "anitamashii";
    }

    @Override
    public boolean if_accept(RobotSendMessage2 data) {
        // 是否被AT

        boolean ated = false;
        boolean name_called = false;
        for (RobotSendMessageSegment segment:
             data.getRobotSendMessage().getMessage()) {
            if (segment.getType().equals("at")) {
                ated = true;
            }

            if (segment.getType().equals("text") && segment.getData().getText().contains(serviceName())) {
                name_called = true;
            }
        }
        logger.info("[Accept] anitamashii service accepted the message.");
        return ated && name_called;

    }


    @Override
    public void running_logic() throws InterruptedException {
        // 自动推送子线程
        Thread autoRss = new Thread(new AutoBilibili());
        autoRss.setDaemon(true);
        autoRss.start();
        while (true) {
            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage(); // 阻塞直到收到消息
            if (isSentToMe()) {
                logger.info("[Service] anitamashii service replied.");
                sendBack(message2);
            }
        }
    }
}
