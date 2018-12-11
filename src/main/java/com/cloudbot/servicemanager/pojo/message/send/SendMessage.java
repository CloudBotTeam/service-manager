package com.cloudbot.servicemanager.pojo.message.send;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送到 bot-manager 的消息
 * @author: Chen Yulei
 * @since: 2018-12-11
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessage {

    private String group_id;

    private String reply;

    //自动转义 默认false
    //消息内容是否作为纯文本发送（即不解析 CQ 码），只在 reply 字段是字符串时有效
    private Boolean auto_escape;
}
