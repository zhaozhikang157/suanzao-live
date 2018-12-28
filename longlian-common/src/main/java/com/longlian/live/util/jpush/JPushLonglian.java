package com.longlian.live.util.jpush;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.model.Platform;
import com.github.pagehelper.StringUtil;
import com.huaxin.util.JPush.JPushClientSender;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.type.MsgType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2016/9/11.
 */
public class JPushLonglian {

    private static final String appKey = "f7af21626a581e1b63707914";    //必填，app所产生的唯一键
    private static final String masterSecret = "603edae92ff446d30fb0435a";//必填，每个应用都对应一个masterSecret
    public static final String title="酸枣在线";
    private static JPushClient jpush = new JPushClient(masterSecret, appKey);
    
    /**
     * 发送给平台所有人通知
     * @param content
     */
    public static void sendToAll(String content) {
        try{
            Map map = new HashMap();
            map.put("NotificationType", MsgType.SYS_OTHER.getTypeStr());
            sendToAll( content,  map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送给部分人通知
     */
    public static void sendToUsers(String[] userMaincheCodes ,  String content , Map<String ,String> map) {
        try{
            JPushClientSender.sendPushNotification(jpush, userMaincheCodes, content, title, map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 发送给平台所有人通知
     * @param content
     */
    public static void sendToAll(String content,Map<String ,String> map) {
        try{
            
            Map<String , String> map2 = getAllCode();
            Collection<String> list = map2.values();
            for (String s : list) {
                sendPushNotificationByCode(s,content,map);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    

    /**
     * 发送给指定的人员
     * @param content
     * @param users
     */
    public static void sendToAppointUser( String content  , String users) {
        try{
            Map map = new HashMap();
            map.put("NotificationType", MsgType.SYS_OTHER.getTypeStr());
            
            String[] user = users.split(",");
            for (String s : user) {
                sendPushNotificationByCode(getCodeByUserId(s),content,map);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 发送消息根据用户ID
     * @param message
     * @param userId
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public static void sendByUserId(String message , String userId) throws APIConnectionException, APIRequestException{
        sendByCode(message ,getCodeByUserId(  userId));
    }
    /**
     * 发送消息根据机器码
     * @param message
     * @param code 机器码
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public static void sendByCode(String message , String code) throws APIConnectionException, APIRequestException{
        JPushClientSender.sendPushObjectByAlias(jpush, message, title, code);
    }
    /**
     * 发送通知根据USERID
     * @param userId
     * @param count
     * @param map
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public  static void sendPushNotificationByUserId(String userId,String count,Map<String ,String> map) throws APIConnectionException, APIRequestException {
        sendPushNotificationByCode(getCodeByUserId(userId),  count, map);
    }
    /**
     * 发送通知根据机器码
     * @param code
     * @param count
     * @param map
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public  static void sendPushNotificationByCode(String code,String count,Map<String ,String> map) throws APIConnectionException, APIRequestException {
        if (StringUtil.isEmpty(code)) {
            return ;
        }

        JPushClientSender.sendPushNotification(jpush, code, count, title, map);
    }

    public static String getCodeByUserId(String userId , RedisUtil redis){
        return redis.hget(RedisKey.ll_jpush_user_code_key, userId);
    }
    
    private static String getCodeByUserId(String userId){
        RedisUtil redis = RedisUtil.getRedisUtil();
        return redis.hget(RedisKey.ll_jpush_user_code_key, userId);
    }
    
    private static Map<String , String> getAllCode() {
        RedisUtil redis = RedisUtil.getRedisUtil();
        return redis.hmgetAll(RedisKey.ll_jpush_user_code_key);
    }

    /**
     * 发送广播通知给平台所有人
     * @param content 内容
     *   @param map 通知类型，URL
     */
    public static void sendBroadcastToAll(String content,Map<String ,String> map) {
        try{
            JPushClientSender.sendBroadcastToAll(jpush, content, title, map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
