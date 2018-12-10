package com.cloudbot.servicemanager.pojo.receive.message;

/**
 * 按类型来区分的消息段
 * @author: Chen Yulei
 * @since: 2018-12-10
 **/
public class MessageSegment {

    //消息段的type：text/url/file/face
    private String type;

    private MessageData messageData;

}
