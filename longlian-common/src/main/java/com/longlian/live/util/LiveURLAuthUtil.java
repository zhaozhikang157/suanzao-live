package com.longlian.live.util;

import com.huaxin.util.MD5Util;
import com.huaxin.util.security.MD5;

import java.util.Date;

/**
 * Created by liuhan on 2017-07-28.
 */
public class LiveURLAuthUtil {

    private static final  String key = "longlian123";

    public static String getPushUrl(String originUrl , String uri) {
        Date now = new Date();
        //10小时失效
        long expireTime = now.getTime()/1000 +  60 * 60 * 12;
        String str = uri + "-"+ expireTime +"-0-0-" + key;
        String md5 =  MD5Util.MD5Encode(str);
        //System.out.println("url:----------------" + originUrl + "&auth_key="+ expireTime +"-0-0-" + md5);
        return originUrl + "&auth_key="+ expireTime +"-0-0-" + md5;
    }


    public static String getPlayUrl(String originUrl, String uri) {
        Date now = new Date();
        //10小时失效
        long expireTime = now.getTime()/1000 +  60 * 60 * 12;
        String str = uri + "-"+ expireTime +"-0-0-" + key;
        String md5 =  MD5Util.MD5Encode(str);

        //System.out.println("url:----------------" + originUrl + "?auth_key="+ expireTime +"-0-0-" + md5);
       // System.out.println("url:----------------" + uri);
        return originUrl + "?auth_key="+ expireTime +"-0-0-" + md5;
    }


}
