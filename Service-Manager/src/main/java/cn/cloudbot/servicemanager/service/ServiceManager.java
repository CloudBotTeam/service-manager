package cn.cloudbot.servicemanager.service;

//import javafx.concurrent.Servicer;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
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
    public void setEchoTest() {
        logger.info("尼玛的，老夫的 POST CONSTRUCT 启动了");
        this.add_servicer("echo");
    }

    private Map<String, Servicer<RobotSendMessage>> servicerMap = new ConcurrentHashMap<>();

    /**
     * 向 这里 发送数据
     * 需要接受的会被接受
     * @param data
     */
    public void async_send_data(RobotSendMessage data) {
        for (Servicer<RobotSendMessage> servicer:
                servicerMap.values()) {
            if (servicer.if_accept(data)) {
                servicer.async_send_data(data);
            }

        }
    }



    void remove_servicer(String servicer_name) {
        Servicer servicer= servicerMap.get(servicer_name);
        servicer.stop_service();
        servicerMap.remove(servicer_name);
    }

    void add_servicer(String service_name) {

        @SuppressWarnings("unchecked")
        Servicer<RobotSendMessage> servicer = (Servicer<RobotSendMessage>) context.getBean(service_name);

        if (servicer == null) {
            throw new RuntimeException("Add Servicer is wrong");
        }

        if (servicerMap.putIfAbsent(service_name, servicer) == null) {
            servicer.start_service();
        }
    }
}