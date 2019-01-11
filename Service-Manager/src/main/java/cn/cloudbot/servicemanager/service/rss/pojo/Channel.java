package cn.cloudbot.servicemanager.service.rss.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 同济大学软件学院通知
 * @author: Chen Yulei
 * @since: 2018-11-29
 **/

@Data //getter setter
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "channel")
public class Channel implements Serializable, Comparable<Channel> {

    //JacksonXmlProperty用法见 https://www.jianshu.com/p/b6b9f8ed8cb7

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "item")
    private ArrayList<ChannelItem> items;

    @Override
    public int compareTo(Channel o) {
        return this.items.get(0).compareTo(o.items.get(0));
    }
}
