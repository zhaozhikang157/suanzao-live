package com.longlian.live.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.PushCacheService;
import com.longlian.live.util.HttpClientManage;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.live.util.SignatureUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by admin on 2018/1/30.
 */
@Service("pushCacheService")
public class PushCacheServiceImpl implements PushCacheService{
    private static Logger log = LoggerFactory.getLogger(PushCacheServiceImpl.class);

    @Autowired
    RedisUtil redisUtil;

    private final long pushCount = 20000;

    /**
     * 缓存
     * @param cacheTs
     */
    @Override
    public void pushCacheUrl(String cacheTs) {
        String time = DateUtil.format(new Date(), "yyyy-MM-dd");
        if(redisUtil.incr(RedisKey.push_object_cache_count + time) <= pushCount){
            redisUtil.expire(RedisKey.push_object_cache_count + time , 60 * 60 * 24 * 2);
            HttpResponse response = null;
            try {
                String url = "https://cdn.aliyuncs.com?";
                String app_key="HdhrU64EnCRlKEmY";
                String Format="JSON";
                String Version="2014-11-11";
                String SignatureMethod="HMAC-SHA1";
                String SignatureVersion="1.0";
                String Action="PushObjectCache";
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
//                param.put("DomainName", "filemedia.llkeji.com");
                param.put("Timestamp", date);
                param.put("Action", Action);
                param.put("ObjectPath" ,cacheTs);
                try {
                    String signature = SignatureUtils.generate("GET", param, LonglianSsoUtil.accessKeySecret);
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
                } else {
                    String str = EntityUtils.toString(response.getEntity(), "utf-8");
                    Map resMap = JsonUtil.getObject(str, HashMap.class);
                    log.info("缓冲 : " + JsonUtil.toJson(resMap));
                }
            }catch (Exception e){
                e.printStackTrace();
                HttpClientManage.dealException(response, e, "");
            }
        }
    }

    /**
     * 预热m3u8
     * @throws IOException
     */
    @Override
    public void pushCacheAddress(String videoAddress) throws IOException {
        URL url = new URL(videoAddress);
        InputStream stream = url.openStream();
        BufferedInputStream buf = new BufferedInputStream(stream);
        StringBuilder sb = new StringBuilder();
        while (true) {
            int data = buf.read();
            if (data == -1) {
                break;
            } else {
                sb.append((char) data);
                if(sb.toString().contains(".ts")){
                    break;
                }
            }
        }
        if(StringUtils.isNotEmpty(sb.toString())){
            String ss[]  = sb.toString().split(",");
            if(ss.length > 0){
                String cacheTs = ss[ss.length - 1].trim();  // 得到.ts
                String address[] = videoAddress.split("/");
                String sp = address[address.length - 1];
                String suffex[] = videoAddress.split(sp); //得到前缀
                String s = suffex[0] + cacheTs;
                log.info("地址:"+s);
                if(StringUtils.isNotEmpty(s)){
                    this.pushCacheUrl(s);
                }
            }
        }
    }

    @Override
    public void pushCachListeUrl(List<String> list) {
        for(int i = list.size() - 1 ; i >= 0 ; i --){
            this.pushCacheUrl(list.get(i));
        }
    }
}
