package cn.cloudbot.servicemanager;

import cn.cloudbot.servicemanager.listener.BackSender;
import cn.cloudbot.servicemanager.listener.MessageSendBacker;
import cn.cloudbot.servicemanager.listener.MessageSendBacker2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


@SpringBootApplication
@EnableDiscoveryClient
@EnableRedisRepositories
@EnableBinding(value = {MessageSendBacker.class, MessageSendBacker2.class})
//@EnableAutoConfiguration
//@ComponentScan
public class ServiceManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceManagerApplication.class, args);
    }
}
