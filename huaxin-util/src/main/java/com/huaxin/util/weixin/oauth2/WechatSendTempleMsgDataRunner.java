package com.huaxin.util.weixin.oauth2;

import com.huaxin.util.Utility;
import com.huaxin.util.weixin.type.WechatTemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by syl on 2017-07-07.
 */
public  class WechatSendTempleMsgDataRunner implements Runnable {
    private static Logger logg = LoggerFactory.getLogger(WechatSendTempleMsgDataRunner.class);
    //邮件发送次数
    public static int sendTime = 0;
    public WechatTemplateMessage wechatTemplateMessage;
    public String openid ;//发送人的opendId
    public String access_token;//公众号token
    public WechatSendTempleMsgDataRunner(String access_token, String openid, WechatTemplateMessage wechatTemplateMessage) {
        this.wechatTemplateMessage = wechatTemplateMessage;
        this.access_token = access_token;
        this.openid = openid;
    }
    @Override
    public void run() {
        if(wechatTemplateMessage != null &&
                !Utility.isNullorEmpty(access_token) &&
                !Utility.isNullorEmpty(openid)){
            try{
                ThirdOAuth2.sendTemplateMessageByOpenid(access_token, openid, wechatTemplateMessage);
            }catch(Exception e){
                logg.error("{},处理报错:{}" + "openid-->" + openid + "；access_token--》" + access_token, this.getClass().getName(), e);
              //  GlobalExceptionHandler.sendEmail(e , "mq错误");
            }
        }
    }

}
