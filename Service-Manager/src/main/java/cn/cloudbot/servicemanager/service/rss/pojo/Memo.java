package cn.cloudbot.servicemanager.service.rss.pojo;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



//@NoArgsConstructor
//@AllArgsConstructor
//@JacksonXmlRootElement(localName = "memo")
@Data //getter setter
public class Memo {
	
	//@JacksonXmlProperty(localName = "id")
    private String id;
	
	//@JacksonXmlElementWrapper(localName = "k5", useWrapping = false)
    private Date wtime ;
    
    private Date rtime;
	
	//@JacksonXmlCData(value = true) // 序列化时是否总是使用 CDATA 块
    private String memo;
    
    //private String tag;
    public  Memo() {}
    public Memo(String ID ,Date WTIME,Date RTIME,String MEMO){
    	
    	this.id=ID;
    	this.wtime=WTIME;
    	this.rtime=RTIME;
    	this.memo=MEMO;
    	
    	try{
    		this.writefile();
    	}catch(IOException e){
    		 e.printStackTrace();
    	}
    	
    }
    
    public void writefile() throws IOException{
    	
    	DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
    	String t1 = fmt.format(this.getRtime());
    	DateFormat fmt2 =new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	String t2 = fmt2.format(this.getRtime());
    	//String time= 
    	String filename =this.id+"_"+t1;
    	File writename = new File("./"+filename); // 相对路径，如果没有则要建立一个新的output。txt文件
		if(!writename.exists()){
			writename.createNewFile(); // 创建新文件
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		out.write(t2+":"+this.memo+"\r\n"); // \r\n即为换行s
		out.flush(); // 把缓存区内容压入文件
		out.close(); // 最后记得关闭文件

    }

    //@Override
    //public int compareTo(Memo o) {
    //    return this.items.get(0).compareTo(o.items.get(0));
    //}
	
	
}
