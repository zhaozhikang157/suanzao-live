package com.huaxin.util.JPush;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;

public class JPushApp {
    private static final String appKey = "aa7ae8089922bdd7c613a77f";    //必填，app所产生的唯一键
    private static final String masterSecret = "f07b1bcc5977603a1c6ea002";//必填，每个应用都对应一个masterSecret
    public static final String title="龙链";
    private static JPushClient jpush = new JPushClient(masterSecret, appKey);
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
        sendPushNotificationByCode( getCodeByUserId(userId),  count, map);
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
        String result = redis.hget(RedisKey.jpush_user_code_key, userId);
        //兼容老版
        if (StringUtils.isEmpty(result)) {
            return userId;
        }
        return result;
    }
}
