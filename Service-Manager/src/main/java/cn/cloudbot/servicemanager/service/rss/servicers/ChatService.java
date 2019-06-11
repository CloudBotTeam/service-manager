package cn.cloudbot.servicemanager.service.rss.servicers;

import cn.cloudbot.common.Message.BotMessage.MessageSegmentType;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessage;
import cn.cloudbot.common.Message.BotMessage.RobotSendMessageSegment;
import cn.cloudbot.common.Message.ServiceMessage.RobotRecvMessage;
import cn.cloudbot.common.Message2.RobotRecvMessage2;
import cn.cloudbot.common.Message2.RobotSendMessage2;
import cn.cloudbot.servicemanager.service.Servicer;
import lombok.Data;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Logger;



@Data
@Component("chat")
public class ChatService extends Servicer<RobotSendMessage2> {


    private Logger logger = Logger.getLogger(ChatService.class.getName());


    private RobotSendMessage message;

    // 是否聊天开关
    private Boolean chatSwitch = false;

    private static final String API_KEY ="b910a60caa2c48d8a36f7a823df08ab8";
    private static final  String API_URL = "http://openapi.tuling123.com/openapi/api/v2";
    private static final String USER_ID ="451427";

    public static String getReqMes(String resMes){
        JSONObject reqJson= new JSONObject();
        int reqType = 0 ;
        reqJson.put("reqType",reqType);
        //输入信息
        JSONObject perception = new JSONObject();
        //输入的文本信息
        JSONObject inputText = new JSONObject();
        //用户输入部分
        inputText.put("text",resMes);
        perception.put("inputText",inputText);

        JSONObject userInfo = new JSONObject();
        userInfo.put("apiKey",API_KEY);
        userInfo.put("userId",USER_ID);
        reqJson.put("perception",perception);
        reqJson.put("userInfo",userInfo);

        return reqJson.toString();
    }


    public static String tulingPost(String url,String reqMes) {

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

    public static String getResultMes(String tulinPostStr){

        JSONObject thesultStr = JSONObject.fromObject(tulinPostStr);
        List<Object> results = (List<Object>) thesultStr.get("results");
        JSONObject resultObj = JSONObject.fromObject(results.get(0));
        JSONObject values = JSONObject.fromObject(resultObj.get("values"));
        return values.get("text").toString();
    }


    @Override
    public String serviceName(){
        return "chat";
    }

    @Override
    public boolean if_accept(RobotSendMessage2 data) {
        // 是否被AT
        boolean ated = false;
        boolean name_called = false;
        for (RobotSendMessageSegment segment:
                data.getRobotSendMessage().getMessage()) {
            if (segment.getType().equals(MessageSegmentType.AT)) {
                ated = true;
            }
//            if (segment.getType().equals(MessageSegmentType.TEXT) && segment.getData().getText().contains(serviceName())) {
//                name_called = true;
//            }
            String beginChat = "聊天";
            String stopChat = "闭嘴";
            if (ated && segment.getType().equals(MessageSegmentType.TEXT) && segment.getData().getText().contains(beginChat)) {
                this.chatSwitch = true;
            }
            if (ated && segment.getType().equals(MessageSegmentType.TEXT) && segment.getData().getText().contains(stopChat)) {
                this.chatSwitch = false;
            }

        }
        logger.info("[Accept] news service accepted the message.");

        return true ;
    }



    @Override
    public void running_logic() throws InterruptedException{


//        String user_input = " ";
//        while(!user_input.equals("exit")){
//
//            RobotSendMessage2 message2 = this.get_data();
//            this.message = message2.getRobotSendMessage();
//
//            //用户输入
//            user_input = this.message.toString();
//
//            String result = getResponse(user_input);
//            robotRecvMessgae.setMessage(result);
//        }

//        String inputStr =" ";
        while(true){

            RobotSendMessage2 message2 = this.get_data();
            this.message = message2.getRobotSendMessage();

            if (this.chatSwitch) {
//                logger.info("chat 服务收到的消息是"+message.toString());
//                logger.info("chat 服务收到的消息是"+message.);

                RobotRecvMessage robotRecvMessage = new RobotRecvMessage();
                StringBuilder responseMessage = new StringBuilder();

                String inputStr = this.message.getMessage()[0].getData().getText();
                String responseStr = getResponse(inputStr);
                //机器人回应的消息内容
                responseMessage.append(responseStr);
                //响应消息
                robotRecvMessage.setMessage(responseMessage.toString());

                sendProcessedDataSingle(robotRecvMessage, message2);
                sendBroadcast(responseMessage.toString());
            }
        }
    }


    //对每一条输入进行回应
    public  String getResponse(String input) throws InterruptedException{

        String reqStr = getReqMes(input);
        String resStr = tulingPost(API_URL,reqStr);
        String resultText = getResultMes(resStr);
        //System.out.println(resultText);

        return resultText;
    }


}
