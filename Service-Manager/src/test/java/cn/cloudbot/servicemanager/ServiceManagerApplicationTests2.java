package cn.cloudbot.servicemanager;

import cn.cloudbot.servicemanager.service.rss.service.ChannelService;
import cn.cloudbot.servicemanager.service.rss.servicers.MemoService;
import cn.cloudbot.common.Message.BotMessage.MessageSegmentType;
import cn.cloudbot.common.Message.BotMessage.RobotMessageSender;
import cn.cloudbot.common.Message.BotMessage.RobotSMSData;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message2.RobotSendMessage2;
import cn.cloudbot.servicemanager.service.ServiceManager;
import cn.cloudbot.servicemanager.service.rss.pojo.ChannelItem;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceManagerApplicationTests2 {

	@Autowired
	 private MemoService memoService;
	
	@Test
    public void testMemo() {
    	 
		
		String cmd = "5分钟后提醒我2333333";
    	RobotSendMessage2 receiveMessage2 = new RobotSendMessage2();
    	RobotSendMessage receiveMessage = new RobotSendMessage();
    	//
    	RobotSendMessageSegment Segment1 = new RobotSendMessageSegment();
    	Segment1.setType(MessageSegmentType.TEXT);
    	RobotSMSData text = new RobotSMSData();
    	text.setText(cmd);
    	Segment1.setData(text);
    	//
    	RobotSendMessageSegment Segment2 = new RobotSendMessageSegment();
    	Segment2.setType(MessageSegmentType.QQ);
    	RobotSMSData qq = new RobotSMSData();
    	qq.setQq("123");
    	Segment2.setData(qq);
    	
    	RobotSendMessageSegment[] message = new RobotSendMessageSegment[]{Segment1,Segment2};
    	receiveMessage.setMessage(message);
    	RobotMessageSender robotMessageSender = new RobotMessageSender();
    	robotMessageSender.setNickname("wwf");
    	receiveMessage.setSender(robotMessageSender);
    	receiveMessage2.setRobotSendMessage(receiveMessage);
    	
    	
    	
    	
    	if(receiveMessage2==null){
    		System.out.println("no");
    	}
		else{
			try {
				memoService.running_logic2(receiveMessage2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	 
    	
    	
    }


}
