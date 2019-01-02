package cn.cloudbot.service;

import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import cn.cloudbot.servicemanager.service.rss.services.WeiboService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Hitoka
 * @since: 2019-01-02
 **/
public class TestWeibo {

    @Autowired
    WeiboService weiboService;

    @Test
    public void testWeibo() {

        System.out.println(weiboService.sendBack());
    }
}
