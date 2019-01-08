package cn.cloudbot.servicemanager.service;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;

import cn.cloudbot.servicemanager.listener.BackSender;
import cn.cloudbot.servicemanager.listener.MessageSendBacker;
import cn.cloudbot.servicemanager.service.rss.pojo.ServicePOJO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.support.MessageBuilder;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public abstract class Servicer<T> implements Runnable {
    private Thread service_running_thread;

    private Logger logger = Logger.getLogger(Servicer.class.getName());

//    /**
//     * Exception in thread "Thread-10" java.lang.NullPointerException
//     at cn.cloudbot.servicemanager.service.Servicer.sendProcessedDataBack(Servicer.java:29)
//     at cn.cloudbot.servicemanager.service.echo.EchoService.sendEcho(EchoService.java:23)
//     at cn.cloudbot.servicemanager.service.echo.EchoService.running_logic(EchoService.java:35)
//     at cn.cloudbot.servicemanager.service.Servicer.run(Servicer.java:82)
//     at java.lang.Thread.run(Thread.java:745)
//
//     if I use new to create servicer
//     */
//    @Autowired
//    private MessageSendBacker sender;

    @Autowired
    private BackSender sender;

//    @Autowired
//    private Source sourceSender;

//    发送消息回给 robot.

    /**
     * 获得服务的固定名称
     * @return
     */
    public abstract String serviceName();

    protected void sendProcessedDataBack(RobotRecvMessage message) {
        sender.sendProcessedDataBack(message);
    }

//    public Servicer(String servicer_name, MessageSendBacker sender) {
//
//        this.servicer_name = servicer_name;
//        this.sender = sender;
////        this.sender = context.getBean(MessageSendBacker.class);
////        if (this.sender == null) {
////            logger.info("sender is still null");
////        }
//    }

    private BlockingDeque<T> data_queue = new LinkedBlockingDeque<>();

    /**
     * 是否接受消息
     * @param data
     * @return
     */
    public abstract boolean if_accept(T data);

    public void async_send_data(T data) {
        if (if_accept(data)) {
            data_queue.add(data);
        }
    }

    /**
     * 封装 的 JSP 模型 的 GET
     * @return null if accpet
     */
    protected T get_data() throws InterruptedException {
        T data = data_queue.take();
        logger.info(this.serviceName() + ": 收到消息 :" + data.toString());
        return data;
    }

    /**
     * 停止工作
     */
    public void stop_service() {
        service_running_thread.interrupt();
    }

    public void start_service() {
        service_running_thread = new Thread(this);
        service_running_thread.start();
    }

    public abstract void running_logic() throws InterruptedException;

    @Override
    public void run() {
        try {
            running_logic();
        } catch (InterruptedException e) {
            return;
        }
    }

    public ServicePOJO toServicePojo() {
        ServicePOJO pojo = new ServicePOJO();
        pojo.setServ_id(this.serviceName());
        pojo.setServ_name(this.serviceName());
        return pojo;
    }
}
