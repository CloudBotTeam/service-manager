package cn.cloudbot.servicemanager.service;

//import javafx.concurrent.Servicer;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceManager {
    private Map<String, Servicer<RobotSendMessage>> servicerMap = new HashMap<>();
    private ArrayList<Thread> threads = new ArrayList<>();

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

    void add_servicer(Servicer<RobotSendMessage> task) {
        task.start_service();
        servicerMap.put(task.getServicer_name(), task);
    }
}
