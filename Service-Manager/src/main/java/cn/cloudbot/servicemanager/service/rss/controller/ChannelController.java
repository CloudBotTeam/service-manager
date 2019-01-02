package cn.cloudbot.servicemanager.service.rss.controller;

import cn.cloudbot.servicemanager.config.RestTemplateConfig;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 输入命令获取
 * @author: Chen Yulei
 * @since: 2018-11-29
 **/

@Component
public class ChannelController {

    @Autowired
    private RestTemplate restTemplate;

    String route = "http://maplewish.cn:1200";

    /**
     * 获取软件学院实时通知（新闻动态
     * @return Rss(json)
     */
//    @RequestMapping(value = "/tju/sse/xwdt", method = RequestMethod.GET)
    public Rss getTJUSSExwdt() {
        Rss rss = restTemplate.getForObject(route + "/tju/sse/xwdt", Rss.class);
        return rss;
    }

    /**
     * 获取某人实时微博 刘人语uid：3029431894
     * @param uid
     * @return Rss(json)
     */
//    @RequestMapping(value = "/weibo/user/{uid}", method = RequestMethod.GET)
    public Rss getWeiboByUserId(String uid) {
        Rss rss = restTemplate.getForObject(route + "/weibo/user/" + uid, Rss.class);
        return rss;
    }

    /**
     * 获取微博热搜
     * @return
     */
    public Rss getWeiboHot() {
        Rss rss = restTemplate.getForObject(route + "weibo/search/hot", Rss.class);
        return rss;
    }

    /**
     * 获取某 up 主的视频列表
     * @param uid
     * @return
     */
    public Rss getBilibiliByUserId(String uid) {
        Rss rss = restTemplate.getForObject(route + "/bilibili/user/video/" + uid, Rss.class);
        return rss;
    }

    /**
     * 获取掘金文章 by type
     * @param type
     * @return
     */
    public Rss getJuejinByType(String type) {
        Rss rss = restTemplate.getForObject(route + "juejin/category/" + type, Rss.class);
        return rss;
    }



}
