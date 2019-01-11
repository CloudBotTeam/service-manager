package cn.cloudbot.servicemanager.service.rss.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: Chen Yulei
 * @since: 2018-11-29
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
//@JacksonXmlRootElement(localName = "item")
public class ChannelItem implements Comparable<ChannelItem>, Serializable{

    @JacksonXmlProperty(localName = "title")
    private String title;

    //可以不获取
    @JacksonXmlProperty(localName = "description")
    private String description;

    @JacksonXmlProperty(localName = "pubDate")
    private Date pubDate;

    @JacksonXmlProperty(localName = "link")
    private String link;


    @Override
    public int compareTo(ChannelItem o) {
        return this.pubDate.compareTo(o.pubDate);
    }
}
