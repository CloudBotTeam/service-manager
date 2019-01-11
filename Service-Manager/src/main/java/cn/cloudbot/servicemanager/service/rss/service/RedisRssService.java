package cn.cloudbot.servicemanager.service.rss.service;

import cn.cloudbot.servicemanager.service.rss.pojo.Rss;

public interface RedisRssService {
    Rss getRssByField(String field);
    void setRssWithField(String field, Rss rss);

    /**
     *
     * @param field Rss 请求的 field
     * @param rss 存储的 RSS DAO
     * @return
     */
    boolean updateIfUpdated(String field, Rss rss);
}
