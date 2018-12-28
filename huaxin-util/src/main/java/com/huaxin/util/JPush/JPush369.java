package com.huaxin.util.JPush;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.model.Platform;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by admin on 2016/9/11.
 */
public class JPush369 {

    private static final String appKey = "80056ad7419c3240db364a1e";    //必填，app所产生的唯一键
    private static final String masterSecret = "0bb72f62c81526485539cd7e";//必填，每个应用都对应一个masterSecret
    public static final String title="龙链369";
    private static JPushClient jpush = new JPushClient(masterSecret, appKey);
    
    /**
     * 发送给平台所有人通知
     * @param content
     */
    public static void sendToAll(String content) {
        try{
            Map map = new HashMap();
            map.put("NotificationType", JPushTypeEnum369.JPUSH_DEFAULT.getCode());
            
            sendToAll(  content,  map);
            //JPushClientSender.sendPushNotification(jpush, Platform.android_ios(), "", content,JPush369.title , map , true);
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
            //JPushClientSender.sendPushNotification(jpush, Platform.android_ios(), "", content,JPush369.title , map , true);
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
            map.put("NotificationType", JPushTypeEnum369.JPUSH_DEFAULT.getCode());
            
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
        JPushClientSender.sendPushNotification(jpush, code, count, title, map);
    }
    
    
    private static String getCodeByUserId(String userId){
        RedisUtil redis = RedisUtil.getRedisUtil();
        return redis.hget(RedisKey.jpush_369user_code_key, userId);
    }
    
    private static Map<String , String> getAllCode() {
        RedisUtil redis = RedisUtil.getRedisUtil();
        return redis.hmgetAll(RedisKey.jpush_369user_code_key);
    }
}
