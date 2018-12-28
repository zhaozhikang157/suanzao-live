package com.longlian.live.util.yunxin;

import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by lh on 2017-02-13.
 */
@Component
public class YunXinUtil {
    private static Logger log = LoggerFactory.getLogger(YunXinUtil.class);

    public static String appKey = "";

    public static String appSecret = "";

    @Value("${yunxin.appKey:}")
     public void setAppKey(String appKey) {
        YunXinUtil.appKey = appKey;
    }

    @Value("${yunxin.appSecret:}")
    public void setAppSecret(String appSecret) {
        YunXinUtil.appSecret = appSecret;
    }

    /**
     * 设置请示参数（标示身份）
     * @param httpPost
     */
    public static void setHttpPostHeader( HttpPost httpPost) {
        String nonce =   getNonce();
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码
        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
    }

    private static String getNonce(){
        int n=0;
        n=(int)(Math.random()*100000);
        while(n<10000 || !handle(n)){
            n=(int)(Math.random()*100000);
        }
        return String.valueOf(n);
    }

    private static boolean handle(int n){
        int[] list=new int[5];
        for(int i=0;i<5;i++){
            list[i]=n%10;
            n=n/10;
        }
        for(int i=0;i<5;i++){
            for(int j=i+1;j<5;j++){
                if(list[i]==list[j]) return false;
            }
        }
        return true;
    }
}
