package cn.cloudbot.servicemanager.service.rss.servicers;

import org.springframework.stereotype.Component;

import cn.cloudbot.common.Message.BotMessage.MessageSegmentType;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.common.Message2.RobotSendMessage2;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.pojo.Memo;

import java.text.ParseException; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


@Component("memo")
public class MemoService extends Servicer<RobotSendMessage2>{

	private static Logger logger = Logger.getLogger(MemoService.class.getName());
	
	private RobotSendMessage message;
	
	private Memo memo;
	
	public enum CMD{
		MEMO,ALL,IN,NULL
	}
	
	CMD cmd= CMD.NULL;
	
	@Override
	public String serviceName() {
		// TODO Auto-generated method stub
		return "memo";
	}

	@Override
	public boolean if_accept(RobotSendMessage2 data) {
		// TODO Auto-generated method stub
		
		 //boolean ated = true;
	     boolean name_called = false;
	     //boolean format = false;
	     
	     for (RobotSendMessageSegment segment:
	            data.getRobotSendMessage().getMessage()) {
	           
	            if (segment.getType().equals(MessageSegmentType.TEXT)) {
	                
	            	if (segment.getData().getText().contains("提醒")
	            			|| segment.getData().getText().contains("安排")) {
	            		name_called = true;
		            }
	            	
	            	
	            }
	            
	            
	        }
	     
	        
		logger.info("[Accept] memo service accepted the message.");
		return name_called;
		//return ated && name_called && format;
	}
	
	
	
	
	

	@Override
	public void running_logic() throws InterruptedException {
		// TODO Auto-generated method stub

		while(true) {

			RobotSendMessage2 message2 = this.get_data();
			this.message = message2.getRobotSendMessage();
			this.memo = new Memo();
			this.memo.setId(this.message.getSender().getNickname());
			setCmd();

			RobotRecvMessage robotRecvMessage = new RobotRecvMessage();

			switch (cmd) {
				case MEMO:
					DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					String t1 = fmt.format(this.memo.getRtime());
					RobotRecvMessage robotRecvMessage1 = new RobotRecvMessage();
					robotRecvMessage1.setMessage("@" + this.memo.getId() + " " + "好的，我将在" +
							t1 + "提醒你" + this.memo.getMemo());
					sendProcessedDataSingle(robotRecvMessage1, message2);

					long diff = this.memo.getRtime().getTime() - new Date().getTime();
					if (diff >= 0) {
						Thread.sleep(diff);
					}

					//RobotRecvMessage robotRecvMessage2 = new RobotRecvMessage();
					robotRecvMessage.setMessage("@" + this.memo.getId() + " 您在" +
							t1 + "有一条备忘录" + this.memo.getMemo());

					//sendProcessedDataSingle(robotRecvMessage, message2);

					break;

				case ALL:
					robotRecvMessage.setMessage("@" + this.memo.getId() + " " + "你所有的备忘录：" +
							":" + readMemo(this.memo.getId()));
					break;

				case IN:
					DateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd");
					String t2 = fmt2.format(this.memo.getRtime());
					robotRecvMessage.setMessage("@" + this.memo.getId() + " " + t2 + "所有的备忘录："
							+ readMemo(this.memo.getId() + "_" + t2));
					break;

				case NULL:
					robotRecvMessage.setMessage("WRONG FORMAT");
					break;
			}

			sendProcessedDataSingle(robotRecvMessage, message2);

		}
     
	}
	
	
	
	public boolean setMemo(){
		
		
		
		for (RobotSendMessageSegment segment:this.message.getMessage()) {
			 
			 
			 if (segment.getType().equals(MessageSegmentType.TEXT)){
				 
				 String txt1 = segment.getData().getText().split("提醒我")[1];
		         //date = StrToDate(txt1);    
		         this.memo.setMemo(txt1);
		          
		         
		         String txt2 = segment.getData().getText().split("分钟")[0];
		         int after = Integer.parseInt(txt2);
		         this.memo.setWtime(new Date());
		         Date afterDate = new Date(this.memo.getWtime().getTime() + after*60000);
		         this.memo.setRtime(afterDate);
		         
		         
		         try{
		     		this.memo.writefile();
		     	}catch(IOException e){
		     		 e.printStackTrace();
		     	}
		         
			 }
				
		 }
		
		
		 return true;
	}
	

	public void setCmd(){
		//memo = new Memo();
		
		
		for (RobotSendMessageSegment segment:this.message.getMessage()) {
			
			
			 if (segment.getType().equals(MessageSegmentType.TEXT)){
				 if (segment.getData().getText().contains("安排") &&
						 !segment.getData().getText().contains("今天")){
					 this.cmd=CMD.ALL;
					
				 }
				 
				 if (segment.getData().getText().contains("安排")&&
						 segment.getData().getText().contains("今天")){
					 this.cmd=CMD.IN;
					
					 this.memo.setRtime(new Date());
				 }
				 
								 
				 if (segment.getData().getText().contains("提醒")){
					 if(setMemo()){
					 this.cmd=CMD.MEMO;
				 	}
			 	}
	
			 }
		
		}
	}
	
	public String readMemo(String info){
		
	     
	     File file = new File("./");   
	        // get the folder list   
	     File[] array = file.listFiles();   
	     if(array.length<=0){
	    	 return "您还没有备忘录";
	     }
	     
	     List<String> list = new ArrayList<String>();
	     
	     
	     for(int i=0;i<array.length;i++){
	    	 
	    	 if(!array[i].getName().contains(info)){
	    		 continue;
	    	 }
	    	 
	    	 try
	         {
	             String encoding = "GBK";
	             File afile =array[i];
	             if (afile.isFile() && afile.exists())
	             { // 判断文件是否存在
	                 InputStreamReader read = new InputStreamReader(
	                         new FileInputStream(afile), encoding);// 考虑到编码格式
	                 BufferedReader bufferedReader = new BufferedReader(read);
	                 String lineTxt = null;

	                 while ((lineTxt = bufferedReader.readLine()) != null)
	                 {
	                     list.add(lineTxt);
	                 }
	                 bufferedReader.close();
	                 read.close();
	             }
	             else
	             {
	                 System.out.println("找不到指定的文件");
	             }
	         }
	         catch (Exception e)
	         {
	             System.out.println("读取文件内容出错");
	             e.printStackTrace();
	         }
	    	 
	    	 
	     }
		
		 
		 StringBuilder rev = new StringBuilder();
		 if(list != null && list.size() > 0){
			 for(int i = 0 ; i < list.size(); i++) {
				 rev.append(i == 0 ? list.get(i) : ";" + list.get(i)); // 使用三元運算子判斷是否為第一筆
			 }
		}else{
			rev.append("您还没有备忘录");
		}
	     return rev.toString();
	     
	}
	
	public Date StrToDate(String str){
		
		DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;    
		try { 
       	  date = fmt.parse(str); 
       } catch (ParseException e) { 
       	  e.printStackTrace(); 
       } 
		return date;
	}
	
	
	//测试用的
	public void running_logic2(RobotSendMessage2 message2) throws InterruptedException {
		// TODO Auto-generated method stub
		
		//RobotSendMessage2 message2 = this.get_data();
        this.message = message2.getRobotSendMessage();
        
        this.memo = new Memo();
        setCmd();
        
        /*
        if(setMemo()){
        	DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        	String t1 = fmt.format(this.memo.getRtime());
        	RobotRecvMessage robotRecvMessage1 = new RobotRecvMessage();
        	 robotRecvMessage1.setMessage("@"+this.memo.getId()+" "+"好的，我将在"+
        			 t1+"提醒你"+this.memo.getMemo());
        	 sendProcessedDataSingle(robotRecvMessage1, message2);
        	
        	long diff= this.memo.getRtime().getTime()-new Date().getTime();
        	if(diff>=0){
        		Thread.sleep(diff);
        	}
        	
        	RobotRecvMessage robotRecvMessage2 = new RobotRecvMessage();
            robotRecvMessage2.setMessage("@"+this.memo.getId()+" 您在"+
            		t1+"有一条备忘录"+this.memo.getMemo());

            sendProcessedDataSingle(robotRecvMessage2, message2);
        	
        }else{
        	
        	RobotRecvMessage robotRecvMessage = new RobotRecvMessage();
            robotRecvMessage.setMessage("WRONG FORMAT");

            sendProcessedDataSingle(robotRecvMessage, message2);
        }*/
        
        RobotRecvMessage robotRecvMessage = new RobotRecvMessage();
        
        switch(cmd){
        case MEMO:
        	DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        	String t1 = fmt.format(this.memo.getRtime());
        	RobotRecvMessage robotRecvMessage1 = new RobotRecvMessage();
        	 robotRecvMessage1.setMessage("@"+this.memo.getId()+" "+"好的，我将在"+
        			 t1+"提醒你"+this.memo.getMemo());
        	 sendProcessedDataSingle(robotRecvMessage1, message2);
        	
        	long diff= this.memo.getRtime().getTime()-new Date().getTime();
        	if(diff>=0){
        		Thread.sleep(diff);
        	}
        	
        	//RobotRecvMessage robotRecvMessage2 = new RobotRecvMessage();
            robotRecvMessage.setMessage("@"+this.memo.getId()+" 您在"+
            		t1+"有一条备忘录"+this.memo.getMemo());

            //sendProcessedDataSingle(robotRecvMessage, message2);
            
        	break;
        	
        case ALL:
        	robotRecvMessage.setMessage("@"+this.memo.getId()+" "+"你所有的备忘录："+
            		":"+readMemo(this.memo.getId()));
        	break;
        case IN:
        	DateFormat fmt2 =new SimpleDateFormat("yyyy-MM-dd");
        	String t2 = fmt2.format(this.memo.getRtime());
        	robotRecvMessage.setMessage("@"+this.memo.getId()+" "+t2+"所有的备忘录："
        								+readMemo(this.memo.getId()+"_"+t2));
        	break;
        
        case NULL:
            robotRecvMessage.setMessage("WRONG FORMAT");
        	break;
        }
        
        sendProcessedDataSingle(robotRecvMessage, message2);
	}
	
}
