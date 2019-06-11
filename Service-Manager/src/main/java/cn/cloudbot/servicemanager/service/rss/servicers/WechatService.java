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

@Data
@Component("wechat")
public class WechatService extends Servicer<RobotSendMessage2> {

    @Autowired
    private ChannelService channelController;

    @Autowired
    private RedisRssService redisRssService;

    private RobotSendMessage message;

    // 公众号名称
    private String account_name;

    // 公众号及对应的 ID
    private HashMap<String, String> accountID = new HashMap<>();


    @Override
    public String serviceName() {
        return "wechat";
    }

    @Override
    public boolean if_accept(RobotSendMessage2 data) {
        // 是否被AT

        //boolean ated = false;
        boolean name_called = false;
        for (RobotSendMessageSegment segment:
                data.getRobotSendMessage().getMessage()) {
//            if (segment.getType().equals(MessageSegmentType.AT)) {
//                ated = true;
//            }

//            if (segment.getType().equals(MessageSegmentType.TEXT) && segment.getData().getText().contains(serviceName())) {
//                name_called = true;
//
//                Iterator iter = accountID.keySet().iterator();
//                while (iter.hasNext()) {
//                    String name = iter.next().toString();
//
//                    if(segment.getData().getText().contains(name)){
//                        this.account_name = name;
//                        break;
//                    }
//                }
//            }

            if (segment.getType().equals(MessageSegmentType.TEXT)) {

                if(segment.getData().getText().contains("公众号") ||
                        segment.getData().getText().contains("推送") ||
                        segment.getData().getText().contains("漫画") ||
                        segment.getData().getText().contains("文章")) {

                    Iterator iter = accountID.keySet().iterator();
                    while (iter.hasNext()) {
                        String name = iter.next().toString();

                        if(segment.getData().getText().contains(name)){
                            name_called = true;
                            this.account_name = name;
                            break;
                        }
                    }

                }

            }
        }
        return name_called;
    }


    @Override
    public void running_logic() throws InterruptedException {

        accountID.put("同济大学", "5c276fa3497ff40f7fe4b81d");
        accountID.put("机器之心", "5b575dd058e5c4583338dbd3");
        accountID.put("非人哉", "5bc5447f244d4e7c65406486");
        accountID.put("游戏时光", "5c5cea53497ff47ac121102c");
        accountID.put("机核", "5b9e723a244d4e7878c5b438");

        List<String> St = new ArrayList<>();
        St.add("说到这个，");
        St.add("既然你们在讨论这个，");
        St.add("你们对这种东西感兴趣吗，那");
        St.add("告诉你们一个秘密，");

        List<String> Mid = new ArrayList<>();
        Mid.add("我发现");
        Mid.add("看起来");
        Mid.add("听说");
        Mid.add("你们知道吗，");

        List<String> Ed = new ArrayList<>();
        Ed.add("发新推送了：");
        Ed.add("发了一些精彩的文章：");
        Ed.add("活过来了：");
        Ed.add("最近有了一些动作：");

        while (true) {

            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage(); // 阻塞直到收到消息

            Rss rss = channelController.getWechatById(accountID.get(account_name));

            RobotRecvMessage robotRecvMessage = new RobotRecvMessage();


            StringBuilder articles = new StringBuilder();


            int StID = (int)(Math.random()*1000) % 4;
            int MidID = (int)(Math.random()*1000) % 4;
            int EdID = (int)(Math.random()*1000) % 4;

            articles.append(St.get(StID) + Mid.get(MidID) + account_name + "公众号" + Ed.get(EdID) + "\n");
            ArrayList<ChannelItem> items = rss.getChannel().getItems();
            for (int i = 0; i < 3; i++) {
                articles.append(items.get(i).getTitle() + '\n');
                articles.append("阅读这篇文章-> " + items.get(i).getLink() + '\n' + '\n');
            }

            robotRecvMessage.setMessage(articles.toString());

            sendProcessedDataSingle(robotRecvMessage, message2);
        }
    }

//    public static void main(String [] args) {
//        WechatService test = new WechatService();
//        test.running_logic();
//    }
}
