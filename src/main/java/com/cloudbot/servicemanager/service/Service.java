package com.cloudbot.servicemanager.service;

import com.cloudbot.servicemanager.sender.MessageSender;

/**
 * @author: Chen Yulei
 * @since: 2018-12-11
 **/
public interface Service {

    void run(String groupId);
}
