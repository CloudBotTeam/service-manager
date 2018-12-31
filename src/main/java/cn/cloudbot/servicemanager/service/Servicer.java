package cn.cloudbot.servicemanager.service;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class Servicer<T> implements Runnable {
    private String servicer_name;
    Thread service_running_thread;

    public Servicer(String servicer_name) {

        this.servicer_name = servicer_name;
    }
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
        return data_queue.take();
    }

    public String getServicer_name() {
        return servicer_name;
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
}
