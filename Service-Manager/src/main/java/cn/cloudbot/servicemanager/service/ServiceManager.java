package cn.cloudbot.servicemanager.service;

//import javafx.concurrent.Servicer;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message2.RobotSendMessage2;
import cn.cloudbot.servicemanager.listener.BackSender;
import cn.cloudbot.servicemanager.listener.MessageSendBacker;
import cn.cloudbot.servicemanager.service.echo.EchoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Configuration
public class ServiceManager {
    private Logger logger = Logger.getLogger(ServiceManager.class.getName());
    private static final ServiceManager serviceManager = new ServiceManager();

    @Autowired
    private ApplicationContext context;

    @Bean
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    @PostConstruct
    public void setAll() {

        this.add_servicer("echo");
        this.add_servicer("anitamashii");
        this.add_servicer("bangumi");
        this.add_servicer("movie");
        this.add_servicer("sse");
        this.add_servicer("hot");
        this.add_servicer("chat");
        this.add_servicer("wechat");
        this.add_servicer("game");
        this.add_servicer("news");

    }

    private Map<String, Servicer<RobotSendMessage2>> servicerMap = new ConcurrentHashMap<>();

    /**
     * 向 这里 发送数据
     * 需要接受的会被接受
     * @param data
     */
    @Deprecated
    public void async_send_data(RobotSendMessage2 data) {
        for (Servicer<RobotSendMessage2> servicer:
                servicerMap.values()) {
            if (servicer.if_accept(data)) {
                servicer.async_send_data(data);
            }

        }
    }

    public void async_send_to_servicer2(RobotSendMessage2 data, String serviceName) {
        Servicer servicer = servicerMap.get(serviceName);
        servicer.async_send_data(data);

    }



    void remove_servicer(String servicer_name) {
        Servicer servicer= servicerMap.get(servicer_name);
        servicer.stop_service();
        servicerMap.remove(servicer_name);
    }

    void add_servicer(String service_name) {

        @SuppressWarnings("unchecked")
        Servicer<RobotSendMessage2> servicer = (Servicer<RobotSendMessage2>) context.getBean(service_name);

        if (servicer == null) {
            throw new RuntimeException("Add Servicer is wrong");
        }

        if (servicerMap.putIfAbsent(service_name, servicer) == null) {
            servicer.start_service();
        }
    }
}
