package cn.cloudbot.servicemanager.pojo.message.receive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 从 botManager 发来的消息
 * @author: Chen Yulei
 * @since: 2018-12-10
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveMessage {

    //收到的消息段「按类型来分段」
    private List<MessageSegment> messageSegments;

    //qq or weChat
    private String platform;

    //qq_group_id or weChat_room_id
    private String id;

    //发出消息的机器人名称
    private String robot_name;

}
