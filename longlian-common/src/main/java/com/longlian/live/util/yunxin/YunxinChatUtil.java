package com.longlian.live.util.yunxin;

import com.alibaba.fastjson.JSONArray;
import com.huaxin.util.JsonUtil;
import com.longlian.live.util.HttpClientManage;
import com.longlian.type.YunxinCustomMsgType;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 云信聊天室相关处理类
 * Created by lh on 2017-02-13.
 */
@Component("yunxinChatUtil")
public class YunxinChatUtil {
    private static Logger log = LoggerFactory.getLogger(YunxinChatUtil.class);
    /**
     * 发送消息
     * @param fromAccid
     * @param msgType
     * @param attach
     * @return
     * @throws Exception
     */
    public Map sendMsg(String fromAccid , String toUser , String msgType   ,String attach   ) {
        HttpResponse response = null;
        try {
            String url = YunXinConst.SEND_CHAT_MSG_URL;
            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            //nvps.add(new BasicNameValuePair("msgId",uuid));
            nvps.add(new BasicNameValuePair("ope","0"));
            nvps.add(new BasicNameValuePair("to",toUser));
            nvps.add(new BasicNameValuePair("from", fromAccid));
            nvps.add(new BasicNameValuePair("type", msgType));
            nvps.add(new BasicNameValuePair("body",attach));


            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("发送消息,发送人:{} ,发送给：{}, 消息类型：{} , 内容：{}, 返回状态码：{}"  , fromAccid , toUser  , msgType ,attach , status);
                throw new Exception("发送消息报错");
            } else {

                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                // 打印执行结果
                log.info(str);

                //{ "desc": { "roomid": 16,  "level": 10, "accid": "zhangsan", "type": "COMMON" },  "code": 200  }
                Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer) msg.get("code");
                if (code == 200) {
                    Map info = (Map) msg.get("desc");
                    return info;
                }
            }
        }catch (Exception e){
            String tip = "发送消息,发送人:" + fromAccid  + ",发送给："+ toUser +", 消息类型：" + msgType + " , 内容：" + attach;
            HttpClientManage.dealException(response , e , tip);
        }
        return null;
    }
    public static Map getSendMap(Integer type , Map data) {
        Map map = new HashMap();
        map.put("type" , type);
        map.put("data" , data);
        return map;
    }

}
