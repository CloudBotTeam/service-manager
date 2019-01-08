package cn.cloudbot.servicemanager.controller;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.pojo.ServicePOJO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServicerMap {
    private Map<String, Servicer<RobotSendMessage>> servicerMap = new ConcurrentHashMap<>();

    public Collection<ServicePOJO> listPojo() {
        Collection<ServicePOJO> servicePOJOS = new ArrayList<>();
        for (Servicer<RobotSendMessage> servicer:
             servicerMap.values()) {
            servicePOJOS.add(servicer.toServicePojo());
        }
        return servicePOJOS;
    }

    public Servicer<RobotSendMessage> getServiceByName(String servicer_name) {
        return servicerMap.get(servicer_name);
    }

    @Autowired
    private ApplicationContext context;

    // TODO: 考虑使用 注解实现
    @PostConstruct
    public void initMap() {
        servicerMap.put("echo", getServicerByName("echo"));
        servicerMap.put("anitamashii", getServicerByName("anitamashii"));
        servicerMap.put("weibo-lin", getServicerByName("weibo-lin"));
        servicerMap.put("sse", getServicerByName("sse"));
    }


    @SuppressWarnings("unchecked")
    private Servicer<RobotSendMessage> getServicerByName(String service_name) {
        return  (Servicer<RobotSendMessage>) context.getBean(service_name);
    }
}
