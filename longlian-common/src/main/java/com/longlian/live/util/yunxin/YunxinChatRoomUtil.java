package com.longlian.live.util.yunxin;

import com.alibaba.fastjson.JSONArray;
import com.huaxin.util.JsonUtil;
import com.longlian.live.util.HttpClientManage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
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
@Component("yunxinChatRoomUtil")
public class YunxinChatRoomUtil {
    private static Logger log = LoggerFactory.getLogger(YunxinChatRoomUtil.class);


    /**
     * 创建聊天室（创建课程的时候创建）
     * @param creator
     * @param name
     * @param courseId
     * @return
     * @throws Exception
     */
    public Integer createRoom(String creator , String name , Long courseId) {
        HttpResponse response = null;
        String tip = "创建聊天室,创建人:" + creator + ",课程ID:" + courseId +", 聊天室名称:" + name;
        log.info(tip);
        try {
            String url = YunXinConst.CREATE_CHAT_ROOM_URL;
            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("creator", creator));
            nvps.add(new BasicNameValuePair("name", name));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("创建聊天室,创建人:{} ,课程ID:{} , 聊天室名称:{}, 返回状态码：{}"  , creator , courseId, name , status);
                throw new Exception("创建聊天室报错");
            } else {

                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                // 打印执行结果
                log.info("创建聊天室:"+str);

                //{"code":200,"info":{"token":"8737a7b1c92965cf44932cd5bc4ce98f","accid":"23"}}
                Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer) msg.get("code");
                if (code == 200) {
                    Map info = (Map) msg.get("chatroom");
                    return (Integer) info.get("roomid");
                }else{
                    log.error("创建聊天室返回参数异常："+JsonUtil.toJson(msg));
                }
            }
        }catch (Exception e){
            log.error(tip + ":{}" , e);
            HttpClientManage.dealException(response , e , tip);
        }
        return 0;
    }

    /**
     * 根据云信accid和云信聊天室ID，取得聊天室的地址(是个数组)
     * @param userId
     * @param roomId
     * @return
     * @throws Exception
     */
    public String[] getChatRoomAddress(String userId , String roomId) {
            HttpResponse response = null;
            try {
                String url = YunXinConst.CHAT_ROOM_ADDR_URL;
                HttpPost httpPost = new HttpPost(url);
                YunXinUtil.setHttpPostHeader(httpPost);
                // 设置请求的参数
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("roomid",  roomId));
                nvps.add(new BasicNameValuePair("accid", userId));
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

                // 执行请求
                response = HttpClientManage.getHttpClient().execute(httpPost);
                int status = response.getStatusLine().getStatusCode();
                if (status != HttpStatus.SC_OK) {
                    EntityUtils.consume(response.getEntity());
                    log.error("取得聊天室地址,用户:{} ,直播间ID:{} , 返回状态码：{}"  , userId , roomId , status);
                    throw new Exception("取得聊天室地址报错");
                } else {

                    String str = EntityUtils.toString(response.getEntity(), "utf-8");
                    // 打印执行结果
                    log.info(str);

                    //{"code":200,"info":{"token":"8737a7b1c92965cf44932cd5bc4ce98f","accid":"23"}}
                    Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                    Integer code = (Integer) msg.get("code");


                    if (code == 200) {
                        JSONArray info = (JSONArray) msg.get("addr");
                        String[] strs = new String[info.size()];
                        for (int i = 0; i < strs.length; i++) {
                            strs[i] = info.getString(i);
                        }
                        return strs;
                    }
                }
            }catch (Exception e){
                String tip = "取得聊天室地址,用户:" + userId + ",直播间ID:" + roomId ;
                log.error(tip + ":{}" , e);
                HttpClientManage.dealException(response , e , tip);
            }
            return new String[]{};
    }

    /**
     * 聊天室开关
     * @param userId
     * @param roomId
     * @param flag
     * @return
     * @throws Exception
     */
    public Boolean toggleCloseRoom(String userId , String roomId , String flag) {
        HttpResponse response = null;
        try {
            String url = YunXinConst.CLOSE_CHAT_ROOM_URL;
            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("roomid",  roomId));
            nvps.add(new BasicNameValuePair("operator", userId));
            nvps.add(new BasicNameValuePair("valid", flag));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("聊天室开关,用户:{} ,直播间ID:{} , 开关：{}, 返回状态码：{}"  , userId , roomId , flag , status);
                throw new Exception("聊天室开关报错");
            } else {

                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                // 打印执行结果
                log.info(str);

                Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer) msg.get("code");
                if (code == 200) {
                    Map info = (Map) msg.get("desc");
                    return (Boolean) info.get("valid");
                }
            }
        }catch (Exception e){
            String tip = "聊天室开关,用户:" + userId + ",直播间ID:" + roomId + ", 开关：" + flag ;
            HttpClientManage.dealException(response , e , tip);
        }
        return false;
    }

    /**
     * 设置聊天室管理员
     * @param roomId
     * @param operator
     * @param target
     * @param opt 1: 设置为管理员，operator必须是创建者
    2:设置普通等级用户，operator必须是创建者或管理员
    -1:设为黑名单用户，operator必须是创建者或管理员
    -2:设为禁言用户，operator必须是创建者或管理员
     * @param optvalue true或false，true:设置；false:取消设置
     * @return
     * @throws Exception
     */
    public Map setRole( String roomId, String operator , String target  , int opt ,String optvalue )  {
        HttpResponse response = null;
        try {

            String url = YunXinConst.SET_CHAT_ROOM_MANAGER_URL;
            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("roomid", roomId));
            nvps.add(new BasicNameValuePair("operator", operator));
            nvps.add(new BasicNameValuePair("target", target));
            nvps.add(new BasicNameValuePair("opt",String.valueOf(opt)));
            nvps.add(new BasicNameValuePair("optvalue", optvalue));


            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            long s = System.currentTimeMillis();
            CloseableHttpClient closeableHttpClient = HttpClientManage.getHttpClient();
            long s1 = System.currentTimeMillis();
            System.out.println("获得连接1：" + (s1 - s));

            response = closeableHttpClient.execute(httpPost);
            long s2 = System.currentTimeMillis();
            System.out.println("执行：" + (s2 - s1));


            int status = response.getStatusLine().getStatusCode();
            log.info("http状态为："+status);
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("设置聊天室权限,操作者:{} ,直播间ID:{} , 角色：{} , 开关：{}, 返回状态码：{}"  , operator , roomId , opt ,optvalue , status);
                throw new Exception("设置聊天室权限报错");
            } else {

                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                // 打印执行结果
                log.info("设置聊天室管理员，返回结果为："+str);

                //{ "desc": { "roomid": 16,  "level": 10, "accid": "zhangsan", "type": "COMMON" },  "code": 200  }
                Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer) msg.get("code");
                if (code == 200) {
                    Map info = (Map) msg.get("desc");
                    log.info("map结果为："+info.get("roomid"));
                    return info;
                }
            }
        }catch (Exception e){
            String tip = "设置聊天室权限,操作者:" + operator + ",直播间ID:" + roomId + ", 角色：" + opt + " , 开关：" + optvalue;
            HttpClientManage.dealException(response , e , tip);
        }
        return null;
    }
    /**
     * 发送消息
     * @param roomId
     * @param fromAccid
     * @param msgType
     * @param attach
     * @return
     * @throws Exception
     */
    public Map sendMsgWithUUID( String roomId, String fromAccid , String msgType   ,String attach , String uuid ) {
        HttpResponse response = null;
        try {
            String url = YunXinConst.SEND_CHAT_ROOM_MSG_URL;
            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("roomid", roomId));
            nvps.add(new BasicNameValuePair("msgId",uuid));
            nvps.add(new BasicNameValuePair("fromAccid", fromAccid));
            nvps.add(new BasicNameValuePair("msgType", msgType));
            nvps.add(new BasicNameValuePair("attach",attach));


            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

            response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("发送消息,发送人:{} ,直播间ID:{} , 消息类型：{} , 开关：{}, 返回状态码：{}"  , fromAccid , roomId , msgType ,attach , status);
                throw new Exception("发送消息报错");
            } else {

                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                // 打印执行结果
                log.info("老师结束直播调用云信返回信息>>>"+str);

                //{ "desc": { "roomid": 16,  "level": 10, "accid": "zhangsan", "type": "COMMON" },  "code": 200  }
                Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer) msg.get("code");
                if (code == 200) {
                    Map info = (Map) msg.get("desc");
                    return info;
                }
            }
        }catch (Exception e){
            String tip = "发送消息,发送人:" + fromAccid + ",直播间ID:" + roomId + ", 消息类型：" + msgType + " , 内容：" + attach;
            HttpClientManage.dealException(response , e , tip);
        }
        return null;
    }

    /**
     * 发送消息
     * @param roomId
     * @param fromAccid
     * @param msgType
     * @param attach
     * @return
     * @throws Exception
     */
    public Map sendMsg( String roomId, String fromAccid , String msgType   ,String attach ) {
        return this.sendMsgWithUUID(roomId,   fromAccid ,   msgType   ,  attach , UUID.randomUUID().toString().replace("-",""));
    }


    /**
     * 上传图片或者语音
     * @return
     */
    public String upload( String content , Long fromAccid , String roomId)  {
        HttpResponse response = null;
        try {
            String url = YunXinConst.UPLOAD_URL;
            HttpPost httpPost = new HttpPost(url);
            YunXinUtil.setHttpPostHeader(httpPost);
            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("content", content));
            //nvps.add(new BasicNameValuePair("type", type));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            response = HttpClientManage.getHttpClient().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
                log.error("上传图片或者语音错误 : status="+status);
                throw new Exception("上传图片或者语音错误");
            } else {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                // 打印执行结果
                log.info(str);
                //{ "desc": { "roomid": 16,  "level": 10, "accid": "zhangsan", "type": "COMMON" },  "code": 200  }
                        Map msg = (Map) JsonUtil.getObject(str, HashMap.class);
                Integer code = (Integer) msg.get("code");
                if (code == 200) {
                    return msg.get("url").toString();
                }
            }
        }catch (Exception e){
            String tip = "发送消息,发送人:" + fromAccid + ",直播间ID:" + roomId +"上传图片或者语音出错";
            HttpClientManage.dealException(response , e , tip);
        }
        return null;
    }

}
