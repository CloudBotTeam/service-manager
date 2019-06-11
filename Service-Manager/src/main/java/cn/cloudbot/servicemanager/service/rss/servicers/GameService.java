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

@Data
@Component("game")
public class GameService extends Servicer<RobotSendMessage2> {

    @Autowired
    private ChannelService channelController;

    @Autowired
    private RedisRssService redisRssService;

    private RobotSendMessage message;


    @Override
    public String serviceName() {
        return "game";
    }

    @Override
    public boolean if_accept(RobotSendMessage2 data) {
        // 是否被AT

        boolean name_called = false;
        for (RobotSendMessageSegment segment:
                data.getRobotSendMessage().getMessage()) {

            if (segment.getType().equals(MessageSegmentType.TEXT)) {

                if(segment.getData().getText().contains("游戏") ||
                        segment.getData().getText().contains("任天堂") ||
                        segment.getData().getText().contains("老任") ||
                        segment.getData().getText().contains("nintendo") ||
                        segment.getData().getText().contains("Nintendo") ||
                        segment.getData().getText().contains("Eshop") ||
                        segment.getData().getText().contains("eshop") ||
                        segment.getData().getText().contains("发售") ||
                        segment.getData().getText().contains("索尼") ||
                        segment.getData().getText().contains("老索")) {
                    name_called = true;
                }
            }
        }
        return name_called;
    }


    @Override
    public void running_logic() throws InterruptedException {

        List<String> St = new ArrayList<>();
        St.add("我听到了我感兴趣的东西，");
        St.add("人类啊，");
        St.add("你们在谈这个吗？那");
        St.add("你们聊得很开心嘛，但");

        List<String> Mid = new ArrayList<>();
        Mid.add("告诉你们吧，");
        Mid.add("我对这个也知道一点，");
        Mid.add("你们看到那个了吗，");
        Mid.add("你们一定不知道，");

        List<String> Ed = new ArrayList<>();
        Ed.add("任天堂天下第一：");
        Ed.add("索尼辣鸡，不如玩任天堂：");
        Ed.add("看看任天堂的游戏吧：");
        Ed.add("Eshop上有新游戏了：");


        while (true) {

            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage(); // 阻塞直到收到消息

            Rss rss = channelController.getEshop();

            RobotRecvMessage robotRecvMessage = new RobotRecvMessage();


            StringBuilder articles = new StringBuilder();

            int StID = (int)(Math.random()*1000) % 4;
            int MidID = (int)(Math.random()*1000) % 4;
            int EdID = (int)(Math.random()*1000) % 4;

            articles.append(St.get(StID) + Mid.get(MidID) + Ed.get(EdID) + "\n");
            ArrayList<ChannelItem> items = rss.getChannel().getItems();

            int sentLength = items.size();
            for (int i = 0; i < sentLength; i++) {
                articles.append(items.get(i).getTitle() + '\n');
                articles.append("剁手-> " + items.get(i).getLink() + '\n' + '\n');
            }

            robotRecvMessage.setMessage(articles.toString());

            sendProcessedDataSingle(robotRecvMessage, message2);

        }
    }

//    public static void main(String [] args) {
//        GameService test = new GameService();
//        test.running_logic();
//    }
}
