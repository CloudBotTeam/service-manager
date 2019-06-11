package cn.cloudbot.servicemanager.service.rss.service;

import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 输入命令获取
 * @author: Chen Yulei
 * @since: 2018-11-29
 **/

@Component
public class ChannelService {

    @Autowired
    private RestTemplate restTemplate;

//    String route = "http://maplewish.cn:1200";
    String route = "http://118.25.157.149:1200";

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
        Rss rss = restTemplate.getForObject(route + "/weibo/search/hot", Rss.class);
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
        Rss rss = restTemplate.getForObject(route + "/juejin/category/" + type, Rss.class);
        return rss;
    }

    /**
     * 获取正在上映的电影
     */
    public Rss getMovie() {
        Rss rss = restTemplate.getForObject(route + "/douban/movie/playing", Rss.class);
        return rss;
    }

    /**
     * 获取当日bilibili放送列表
     */
    public Rss getBiliToday() {
        Rss rss = restTemplate.getForObject(route + "/bangumi/calendar/today", Rss.class);
        return rss;
    }

    /**
     * 获取某公众号最新推送（按公众号 ID）
     * @param uid
     * @return Rss(json)
     */
    public Rss getWechatById(String uid) {
        Rss rss = restTemplate.getForObject(route + "/wechat/wasi/" + uid, Rss.class);
        return rss;
    }

    /**
     * 根据央视最新新闻
     */
    public Rss getNews() {
        Rss rss = restTemplate.getForObject(route + "/cctv/world", Rss.class);

        return rss;
    }

    /**
     * 获取任天堂新发售的游戏
     * @return Rss(json)
     */
    public Rss getEshop() {
        Rss rss = restTemplate.getForObject(route + "/nintendo/eshop/hk", Rss.class);
        return rss;
    }
}
