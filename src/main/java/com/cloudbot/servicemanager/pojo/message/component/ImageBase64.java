package com.cloudbot.servicemanager.pojo.message.component;

import lombok.AllArgsConstructor;

/**
 * base64 编码的图片
 * @author: Chen Yulei
 * @since: 2018-12-11
 **/
@AllArgsConstructor
public class ImageBase64 extends MessageComponent{

    public String base64;

    @Override
    public String toString()
    {
        return "[CQ:image,file=base64://" + base64 + "]";
    }
}
