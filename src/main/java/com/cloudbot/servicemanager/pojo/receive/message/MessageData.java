package com.cloudbot.servicemanager.pojo.receive.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Chen Yulei
 * @since: 2018-12-10
 **/
@Data
@AllArgsConstructor
public class MessageData {

    private final String text;
    private final String url;
    private final String file;
    private final String face;
}
