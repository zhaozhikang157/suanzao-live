package com.longlian.live.util.aliyun;

import com.huaxin.util.JsonUtil;
import com.longlian.live.util.HttpClientManage;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.live.util.SignatureUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liuhan on 2017-11-18.
 */
public  class MixLiveUtil   {
    public static void startMultipleStreamMixService(String domain , String appName, String stream) {
        HttpResponse response = null;
        long i = 0;
        try {
            String url = "https://live.aliyuncs.com/?";

            String app_key="HdhrU64EnCRlKEmY";
            String Format="JSON";
            String Version="2016-11-01";
            String SignatureMethod="HMAC-SHA1";
            String SignatureVersion="1.0";
            String Action="StartMultipleStreamMixService";

            Map<String,String> param = new HashMap<String,String>();
            param.put("AccessKeyId", app_key);
            param.put("Format", Format);
            param.put("Version", Version);
            param.put("SignatureMethod", SignatureMethod);
            param.put("SignatureVersion", SignatureVersion);
            param.put("SignatureNonce", UUID.randomUUID().toString());
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(tz);
            String date =  df.format(new Date());

            // System.out.println(date);
            param.put("Timestamp", date);
            param.put("Action", Action);
            param.put("MixTemplate" ,"pip4a" );
            param.put("DomainName" ,domain);
            param.put("AppName" , appName);
            param.put("StreamName" ,stream );
            try {
                String signature = SignatureUtils.generate("GET",param, LonglianSsoUtil.accessKeySecret);
                param.put("Signature" ,signature );
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (String key : param.keySet()) {
                url += key + "=" + param.get(key) + "&";
            }

            url = url.substring(0 , url.length() - 1);
            //System.out.println(url);
            HttpGet httpGet = new HttpGet(url);

            response = HttpClientManage.getHttpClient().execute(httpGet);
            int status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.SC_OK) {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(str);
            } else {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                Map resMap = JsonUtil.getObject(str , HashMap.class);
                System.out.println(str);
            }
        }catch (Exception e){
            e.printStackTrace();
            HttpClientManage.dealException(response , e , "");
        }
    }


    public static void addMultipleStreamMixService(String domain , String appName, String stream , String mixDomain, String mixAppName, String mixStream) {
        HttpResponse response = null;
        long i = 0;
        try {
            String url = "https://live.aliyuncs.com/?";

            String app_key="HdhrU64EnCRlKEmY";
            String Format="JSON";
            String Version="2016-11-01";
            String SignatureMethod="HMAC-SHA1";
            String SignatureVersion="1.0";
            String Action="AddMultipleStreamMixService";

            Map<String,String> param = new HashMap<String,String>();
            param.put("AccessKeyId", app_key);
            param.put("Format", Format);
            param.put("Version", Version);
            param.put("SignatureMethod", SignatureMethod);
            param.put("SignatureVersion", SignatureVersion);
            param.put("SignatureNonce", UUID.randomUUID().toString());
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(tz);
            String date =  df.format(new Date());

            // System.out.println(date);
            param.put("Timestamp", date);
            param.put("Action", Action);
            param.put("DomainName" ,domain);
            param.put("AppName" , appName);
            param.put("StreamName" ,stream );

            param.put("MixDomainName",mixDomain);
            param.put("MixAppName" , mixAppName);
            param.put("MixStreamName" ,mixStream );

            try {
                String signature = SignatureUtils.generate("GET",param, LonglianSsoUtil.accessKeySecret);
                param.put("Signature" ,signature );
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (String key : param.keySet()) {
                url += key + "=" + param.get(key) + "&";
            }

            url = url.substring(0 , url.length() - 1);
            //System.out.println(url);
            HttpGet httpGet = new HttpGet(url);

            response = HttpClientManage.getHttpClient().execute(httpGet);
            int status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.SC_OK) {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(str);
            } else {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(str);
                Map resMap = JsonUtil.getObject(str , HashMap.class);
            }
        }catch (Exception e){
            e.printStackTrace();
            HttpClientManage.dealException(response , e , "");
        }
    }
    public static Map<String,String> getParam(String Action){
        String app_key="HdhrU64EnCRlKEmY";
        String Format="JSON";
        String Version="2016-11-01";
        String SignatureMethod="HMAC-SHA1";
        String SignatureVersion="1.0";

        Map<String,String> param = new HashMap<String,String>();
        param.put("AccessKeyId", app_key);
        param.put("Format", Format);
        param.put("Version", Version);
        param.put("SignatureMethod", SignatureMethod);
        param.put("SignatureVersion", SignatureVersion);
        param.put("SignatureNonce", UUID.randomUUID().toString());
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        String date =  df.format(new Date());

        // System.out.println(date);
        param.put("Timestamp", date);
        param.put("Action", Action);
        return param;
    }

    public static void addMixConfig(String domain , String appName) {
        HttpResponse response = null;
        try {
            String url = "https://live.aliyuncs.com/?";
            Map<String,String> param =  getParam("AddLiveMixConfig");
            param.put("Template" ,"mhd" );
            param.put("DomainName" ,domain);
            param.put("AppName" , appName);
            try {
                String signature = SignatureUtils.generate("GET",param, LonglianSsoUtil.accessKeySecret);
                param.put("Signature" ,signature );
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (String key : param.keySet()) {
                url += key + "=" + param.get(key) + "&";
            }

            url = url.substring(0 , url.length() - 1);
            //System.out.println(url);
            HttpGet httpGet = new HttpGet(url);

            response = HttpClientManage.getHttpClient().execute(httpGet);
            int status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.SC_OK) {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(str);
            } else {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                Map resMap = JsonUtil.getObject(str , HashMap.class);
                System.out.println(str);
            }
        }catch (Exception e){
            e.printStackTrace();
            HttpClientManage.dealException(response , e , "");
        }
    }

    public static void delMixConfig(String domain , String appName) {
        HttpResponse response = null;
        try {
            String url = "https://live.aliyuncs.com/?";
            Map<String,String> param =  getParam("DeleteLiveMixConfig");
            param.put("DomainName" ,domain);
            param.put("AppName" , appName);
            try {
                String signature = SignatureUtils.generate("GET",param, LonglianSsoUtil.accessKeySecret);
                param.put("Signature" ,signature );
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (String key : param.keySet()) {
                url += key + "=" + param.get(key) + "&";
            }

            url = url.substring(0 , url.length() - 1);
            //System.out.println(url);
            HttpGet httpGet = new HttpGet(url);

            response = HttpClientManage.getHttpClient().execute(httpGet);
            int status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.SC_OK) {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(str);
            } else {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                Map resMap = JsonUtil.getObject(str , HashMap.class);
                System.out.println(str);
            }
        }catch (Exception e){
            e.printStackTrace();
            HttpClientManage.dealException(response , e , "");
        }
    }

    public static void stopMultipleStreamMixService(String mainMixDomain, String mainMixApp, String mainMixStream) {
        HttpResponse response = null;
        long i = 0;
        try {
            String url = "https://live.aliyuncs.com/?";

            String app_key="HdhrU64EnCRlKEmY";
            String Format="JSON";
            String Version="2016-11-01";
            String SignatureMethod="HMAC-SHA1";
            String SignatureVersion="1.0";
            String Action="StopMultipleStreamMixService";

            Map<String,String> param = new HashMap<String,String>();
            param.put("AccessKeyId", app_key);
            param.put("Format", Format);
            param.put("Version", Version);
            param.put("SignatureMethod", SignatureMethod);
            param.put("SignatureVersion", SignatureVersion);
            param.put("SignatureNonce", UUID.randomUUID().toString());
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(tz);
            String date =  df.format(new Date());

            // System.out.println(date);
            param.put("Timestamp", date);
            param.put("Action", Action);
            param.put("DomainName" ,mainMixDomain);
            param.put("AppName" , mainMixApp);
            param.put("StreamName" ,mainMixStream );
            try {
                String signature = SignatureUtils.generate("GET",param, LonglianSsoUtil.accessKeySecret);
                param.put("Signature" ,signature );
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (String key : param.keySet()) {
                url += key + "=" + param.get(key) + "&";
            }

            url = url.substring(0 , url.length() - 1);
            //System.out.println(url);
            HttpGet httpGet = new HttpGet(url);

            response = HttpClientManage.getHttpClient().execute(httpGet);
            int status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.SC_OK) {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(str);
            } else {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                Map resMap = JsonUtil.getObject(str , HashMap.class);
                System.out.println(str);
            }
        }catch (Exception e){
            e.printStackTrace();
            HttpClientManage.dealException(response , e , "");
        }
    }
}
