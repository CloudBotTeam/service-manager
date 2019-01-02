package cn.cloudbot.servicemanager.service.rss.controller;

import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

/**
 * 实时获取最新的一条推送（也就是说连续更新的两条不一定能通知到
 * @author: Chen Yulei
 * @since: 2018-11-29
 **/

@Controller
public class RealTimeController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //每天中午12点请求一次 从第二天12点开始生效
    @Scheduled(cron = "0 0 12 * * ?")
    public String getTJUSSExwdt() {
        logger.info("请求一次软件学院官网新闻动态");
        Rss rss = restTemplate.getForObject("https://rsshub.app/tju/sse/xwdt", Rss.class);

        Rss redisRss = (Rss)redisTemplate.opsForValue().get("xwdt");
        if (redisRss == null) {
            redisTemplate.opsForValue().set("xwdt", rss);
            return null;
        }
        else {
            if (rss.getChannel().getItems().get(0).getPubDate() == redisRss.getChannel().getItems().get(0).getPubDate()) {
                return null; // 没有新通知
            }
            else {
                redisTemplate.opsForValue().set("xwdt", rss);
                return "学院官网有新通知啦："+ rss.getChannel().getItems().get(0).getTitle() +
                        "\n点击查看->" + rss.getChannel().getItems().get(0).getLink();
            }
        }
    }

    //每3分钟请求一次刘人语3029431894的微博 从第二个3分钟开始生效
    @Scheduled(cron = "0 0/3 * * * ?")
    public String getWeiboReyi(){
        logger.info("请求一次刘人语小朋友的微博动态");
        Rss rss = restTemplate.getForObject("http://rsshub.app/weibo/user/3029431894", Rss.class);
        System.out.println(rss);
        Rss redisRss = (Rss)redisTemplate.opsForValue().get("user3029431894");
        if (redisRss == null) {
            redisTemplate.opsForValue().set("user3029431894", rss);
            return null;
        }
        else {
            if (rss.getChannel().getItems().get(0).getPubDate() == redisRss.getChannel().getItems().get(0).getPubDate()) {
                return null;
            }
            else {
                redisTemplate.opsForValue().set("user3029431894", rss);
                return "66发新微博啦：" + rss.getChannel().getItems().get(0).getTitle() +
                        "\n点击查看->" + rss.getChannel().getItems().get(0).getLink();
            }
        }
    }
}
