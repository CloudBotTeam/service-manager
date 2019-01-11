package cn.cloudbot.servicemanager.service.rss.service;


import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RedisRssServiceImpl implements RedisRssService {
    private static final String KEY_PREFIX = "Rss";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private ValueOperations<String, Object> valueOperations;

    @PostConstruct
    private void init() {
        valueOperations = redisTemplate.opsForValue();
    }

    private String buildRssKey(String field) {
        return KEY_PREFIX + ":" + field;
    }
    public Rss getRssByField(String field) {
        return (Rss) valueOperations.get(buildRssKey(field));
    }

    @Override
    public void setRssWithField(String field, Rss rss) {
        valueOperations.set(buildRssKey(field), rss);
    }

    @Override
    public boolean updateIfUpdated(String field, Rss rss) {
        Rss lastRss = (Rss) valueOperations.getAndSet(field, rss);
        return lastRss.equals(rss);
    }

}
