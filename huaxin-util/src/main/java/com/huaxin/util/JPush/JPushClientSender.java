package com.huaxin.util.JPush;


import java.util.Map;

import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 极光推送
 */
public class JPushClientSender {

    protected static final Logger LOG = LoggerFactory.getLogger(JPushClientSender.class);

    protected static boolean jPushEnv = Boolean.parseBoolean(CustomizedPropertyConfigurer.getContextProperty("jPush_env"));

    /**
     * 推送根据alias
     *
     * @param message 推送的消息
     * @param title   推送的标题
     * @param alias   所推的别名
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public static void sendPushObjectByAlias(JPushClient jpush, String message, String title, String alias) {
        LOG.info("jPush jPushEnv" + jPushEnv);
        if (StringUtils.isEmpty(alias)) {
            LOG.info("Got alias is null ");
            return;
        }
        PushPayload payload = buildPushObject(message, title, alias);
        try {
            PushResult result = jpush.sendPush(payload);
            LOG.info("Got result - " + result);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
        }
    }


    /**
     * 根据定义的别名 推送
     *
     * @param message
     * @param title
     * @param alias
     * @return
     */
    private static PushPayload buildPushObject(String message, String title, String alias) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.alias(alias))
                        .build())
                .setMessage(Message.newBuilder().setTitle(title)
                        .setMsgContent(message)
                        .addExtra("from", "JPush")
                        .build()).setOptions(Options.newBuilder()
                        .setApnsProduction(jPushEnv)
                        .build())
                .build();
    }


    /**
     * Android and IOS 并集 推送 通知
     *
     * @param alias 别名
     * @param content 内容
     * @param title 标题
     * @param map   附加参数
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public static void sendPushNotification(JPushClient jpush, String alias, String content, String title, Map<String, String> map) {
        sendPushNotification(  jpush,new String[]{alias} ,   content,   title,   map);
    }

    /**
     * Android and IOS 并集 推送 通知
     *
     * @param alias 别名
     * @param count 内容
     * @param title 标题
     * @param map   附加参数
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public static void sendPushNotification(JPushClient jpush, String[] alias, String count, String title, Map<String, String> map) {
        sendPushNotification( jpush  , Platform.android_ios(),   alias,   count,   title,   map);
    }
    
    /**
     * Android and IOS 并集 推送 通知
     *
     * @param alias 别名
     * @param count 内容
     * @param title 标题
     * @param map   附加参数
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public static void sendPushNotification(JPushClient jpush, Platform platform,  String[] alias, String count, String title, Map<String, String> map) {
        LOG.info("jPush jPushEnv" + jPushEnv);
        if (alias.length == 0 || StringUtils.isEmpty(alias[0])) {
            LOG.info("Got alias is null ");
            return;
        }
        PushPayload payload = buildPushNotification(platform,alias, count, title, map );
        try {
            PushResult result = jpush.sendPush(payload);
            LOG.info("Got result - " + result);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
        }
    }

    /**
     * Android and IOS 并集 推送通知
     *
     * @param alias 别名
     * @param content 内容
     * @param title 标题
     * @param map   附加字段
     * @return
     */
    private static PushPayload buildPushNotification(Platform platform, String[] alias, String content, String title, Map<String, String> map) {
        Audience   audience = Audience.alias(alias);
        return buildPushNotification(  platform,   content,   title,  map ,   audience );
    }
    /**
     * Android and IOS 并集 推送通知
     *
     * @param content 内容
     * @param title 标题
     * @param map   附加字段
     * @return
     */
    private static PushPayload buildPushNotification(Platform platform, String content, String title, Map<String, String> map , Audience audience ) {
        return PushPayload.newBuilder()
                .setPlatform(platform)
                .setAudience(audience)
                .setNotification(Notification.newBuilder()
                        .setAlert(content)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title).addExtras(map).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtras(map).build())
                        .build()).
                        setOptions(Options.newBuilder()
                                .setApnsProduction(jPushEnv)
                                .build())
                .build();
    }
    /**
     * 推送根据广播
     *
     * @param message 推送的消息
     * @param title   推送的标题
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public static void sendBroadcastToAll(JPushClient jpush, String message, String title,Map<String ,String> map) {
        LOG.info("jPush jPushEnv" + jPushEnv);
        PushPayload payload = buildPushNotification(Platform.android_ios(),message, title, map,Audience.all());
        try {
            PushResult result = jpush.sendPush(payload);
            LOG.info("Got result - " + result);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
        }
    }
}
