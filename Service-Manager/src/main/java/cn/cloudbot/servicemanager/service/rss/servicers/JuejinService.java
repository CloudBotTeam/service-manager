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

import java.util.*;

import java.util.Scanner;

/**
 * @author: Hitoka
 * @since: 2019-01-02
 **/



@Data
@Component("juejin")
public class JuejinService extends Servicer<RobotSendMessage2> {

    @Autowired
    private ChannelService channelController;

    @Autowired
    private RedisRssService redisRssService;

    private RobotSendMessage message;

    // "frontend", "backend", "android", "ios", "ai"
    private String type;

    @Override
    public String serviceName() {
        return "juejin";
    }

    @Override
    public boolean if_accept(RobotSendMessage2 data) {

        boolean name_called = false;
        for (RobotSendMessageSegment segment:
                data.getRobotSendMessage().getMessage()) {

            if (segment.getType().equals(MessageSegmentType.TEXT)) {

                if(segment.getData().getText().contains("掘金") ||
                        segment.getData().getText().contains("程序") ||
                        segment.getData().getText().contains("编程") ||
                        segment.getData().getText().contains("技术") ||
                        segment.getData().getText().contains("开发") ||
                        segment.getData().getText().contains("文章")) {

                    if(segment.getData().getText().contains("前端") ||
                            segment.getData().getText().toLowerCase().contains("frontend"))
                    {
                        name_called = true;
                        this.type = "frontend";
                    }

                    else if(segment.getData().getText().contains("后端") ||
                            segment.getData().getText().toLowerCase().contains("backend"))
                    {
                        name_called = true;
                        this.type = "backend";
                    }

                    else if(segment.getData().getText().contains("安卓") ||
                            segment.getData().getText().toLowerCase().contains("android"))
                    {
                        name_called = true;
                        this.type = "android";
                    }

                    else if(segment.getData().getText().toLowerCase().contains("ios") ||
                            segment.getData().getText().toLowerCase().contains("apple") ||
                            segment.getData().getText().contains("苹果") ||
                            segment.getData().getText().contains("水果"))
                    {
                        name_called = true;
                        this.type = "ios";
                    }

                    else if(segment.getData().getText().contains("人工智能") ||
                            segment.getData().getText().contains("人工智障") ||
                            segment.getData().getText().contains("机器学习") ||
                            segment.getData().getText().toLowerCase().contains("machine learning") ||
                            segment.getData().getText().toLowerCase().contains("ml") ||
                            segment.getData().getText().contains("深度学习") ||
                            segment.getData().getText().toLowerCase().contains("deep learning") ||
                            segment.getData().getText().toLowerCase().contains("dl") ||
                            segment.getData().getText().contains("神经网络") ||
                            segment.getData().getText().contains("计算机视觉") ||
                            segment.getData().getText().toLowerCase().contains("computer vision") ||
                            segment.getData().getText().toLowerCase().contains("cv") ||
                            segment.getData().getText().contains("自然语言处理") ||
                            segment.getData().getText().toLowerCase().contains("nlp") ||
                            segment.getData().getText().toLowerCase().contains("ai"))
                    {
                        name_called = true;
                        this.type = "ai";
                    }
                }

            }
        }
        return name_called;
    }


    @Override
    public void running_logic() throws InterruptedException {

        List<String> St = new ArrayList<>();
        St.add("为什么你们什么技术栈都不会，却还在这里聊天？");
        St.add("群里都是程序员吗，那");
        St.add("我似乎听到有人在谈代码，那");
        St.add("我是代码之神，人类啊，");

        List<String> Mid = new ArrayList<>();
        Mid.add("快关注一下掘金上新出现的");
        Mid.add("快看看掘金上");
        Mid.add("你们应该学习，比如掘金上有");
        Mid.add("又到了学技术的时候了，来看看掘金上");

        while (true) {

            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage(); // 阻塞直到收到消息

            Rss rss = channelController.getJuejinByType(type);

            RobotRecvMessage robotRecvMessage = new RobotRecvMessage();

            StringBuilder articles = new StringBuilder();

            int StID = (int)(Math.random()*1000) % 4;
            int MidID = (int)(Math.random()*1000) % 4;

            articles.append(St.get(StID) + Mid.get(MidID));
            ArrayList<ChannelItem> items = rss.getChannel().getItems();
            for (int i = 0; i < 3; i++) {
                articles.append(' ' + items.get(i).getTitle());
            }

            if(type == "ios") articles.append(" 这些跟 iOS 开发有关的文章！\n\n阅读这些文章👉 https://juejin.im/welcome/ios");
            else if(type == "android") articles.append(" 这些跟安卓开发有关的文章！\n\n阅读这些文章👉 https://juejin.im/welcome/android");
            else if(type == "frontend") articles.append(" 这些跟前端开发有关的文章！\n\n阅读这些文章👉 https://juejin.im/welcome/frontend");
            else if(type == "backend") articles.append(" 这些跟后端开发有关的文章！\n\n阅读这些文章👉 https://juejin.im/welcome/backend");
            else if(type == "ai") articles.append(" 这些跟人工智障有关的文章！\n\n阅读这些文章👉 https://juejin.im/welcome/ai");

            robotRecvMessage.setMessage(articles.toString());
            sendProcessedDataSingle(robotRecvMessage, message2);
        }
    }

//    public static void main(String [] args) {
//        JuejinService test = new JuejinService();
//        test.running_logic();
//    }
}