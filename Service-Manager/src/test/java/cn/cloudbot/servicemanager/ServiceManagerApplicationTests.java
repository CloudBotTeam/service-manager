package cn.cloudbot.servicemanager;

import cn.cloudbot.servicemanager.service.rss.service.ChannelService;
import cn.cloudbot.servicemanager.service.rss.pojo.ChannelItem;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceManagerApplicationTests {

    @Autowired
    ChannelService channelController;

    @Test
    public void contextLoads() {
        //测试发送带图片的消息
        //服务->MessageSender.send(Message)
        //Message.group_id=
    }

    @Test
    public void testMovie() {
        Rss rss = channelController.getMovie();
        StringBuilder hot = new StringBuilder();
        ArrayList<ChannelItem> items = rss.getChannel().getItems();
        for (int i = 0; i < 10; i++) {
            hot.append(items.get(i).getTitle() + '\n');
        }
        hot.append("查看更多->https://movie.douban.com/cinema/nowplaying");
        System.out.println(hot.toString());
    }

    @Test
    public void testJuejin() {
        Rss rss = channelController.getJuejinByType("frontend");
        StringBuilder hot = new StringBuilder();
        ArrayList<ChannelItem> items = rss.getChannel().getItems();
        for (int i = 0; i < 5; i++) {
            hot.append(items.get(i).getTitle() + '\n');
        }
        hot.append("查看更多->https://juejin.im/welcome/" + "frontend");
        System.out.println(hot.toString());
    }

    @Test
    public void testBiliToday() {
        Rss rss = channelController.getBiliToday();
        StringBuilder hot = new StringBuilder();
        ArrayList<ChannelItem> items = rss.getChannel().getItems();
        for (int i = 0; i < items.size(); i++) {
            hot.append(items.get(i).getTitle() + '\n');
        }
        System.out.println(hot.toString());
    }

}
