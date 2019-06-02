package cn.cloudbot.servicemanager.service.rss.servicers;

import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.servicemanager.service.Servicer;
import cn.cloudbot.servicemanager.service.rss.service.ChannelService;
import cn.cloudbot.servicemanager.service.rss.pojo.Rss;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;


@Data
@Component("chat")

public class ChatService extends Servicer<RobotSendMessage2>{

    @Autowired
    private static final String API_KEY ="fe6314779ad64373ae1b189562c90913";
    private static final  String API_URL = "http://openapi.tuling123.com/openapi/api/v2";
    private static final String USER_ID ="451427";

    public ChatRobotInit(){
    }

    public String getReqMes(String reqMes){
        JSONObject reqJson= new JSONObject();
        int reqType = 0 ;
        reqJson.put("reqType",reqType);
        //输入信息
        JSONObject perception = new JSONObject();
        //输入的文本信息
        JSONObject inputText = new JSONObject();
        //用户输入部分
        inputText.put("text",reqMes);
        perception.put("inputText",inputText);

        JSONObject userInfo = new JSONObject();
        userInfo.put("apiKey",API_KEY);
        userInfo.put("userId",USER_ID);
        reqJson.put("perception",perception);
        reqJson.put("userInfo",userInfo);

        return reqJson.toString();
    }

    public String tulingPost(String url,String reqMes) {

        String status = " ";
        String responseStr = " ";
        PrintWriter out = null ;
        BufferedReader in = null ;
        try{
            URL realUrl = new URL(url);
            URLConnection connection =realUrl.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("x-adviewrtb-version", "2.1");

            // 发送POST请求
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            out.write(reqMes);
            out.flush();
            httpURLConnection.connect();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                responseStr += line;
            }
            status = new Integer(httpURLConnection.getResponseCode()).toString();
//            System.out.println("status"+status);

        } catch (java.net.MalformedURLException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return responseStr;
    }

    public String getResultMes(String tulinPostStr){

        JSONObject thesultStr = JSONObject.fromObject(tulinPostStr);
        List<Object> results = (List<Object>) thesultStr.get("results");
        JSONObject resultObj = JSONObject.fromObject(results.get(0));
        JSONObject values = JSONObject.fromObject(resultObj.get("values"));
        return values.get("text").toString();
    }

    //每次对话执行一次此函数
    public String run(String reqMes){

        String reqStr  = new ChatService().getReqMes(reqMes);
        String respStr = new ChatService().tulingPost(API_URL,reqStr);
        String resultText = new ChatService().getResultMes(respStr);

        return resultText;
    }



}
