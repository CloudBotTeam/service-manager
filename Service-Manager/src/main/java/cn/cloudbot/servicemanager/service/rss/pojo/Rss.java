package cn.cloudbot.servicemanager.service.rss.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Chen Yulei
 * @since: 2018-11-29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "rss")
public class Rss implements Serializable, Comparable<Rss>{

    @JacksonXmlProperty(localName = "channel")
    private Channel channel;

    @Override
    public int compareTo(Rss o) {
        return this.channel.compareTo(o.channel);
    }
}
