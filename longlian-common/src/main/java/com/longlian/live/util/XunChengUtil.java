package com.longlian.live.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by admin on 2017/4/12.
 */
@Service("certificationUtil")
public class XunChengUtil {

    @Value("${xuncheng.key:}")
    private String key;

    @Value("${xuncheng.url:}")
    private String url;
    
    private static Logger log = LoggerFactory.getLogger(XunChengUtil.class);
    
    public  String identityCertification(String cardNo, String realName)  throws Exception{
        String response = null;
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);
//        realName = URLEncoder.encode(realName,"utf-8");
        String queryString ="key=" + key + "&cardNo=" + cardNo + "&realName=" + realName;
        log.info(queryString);
        try {
            if (StringUtils.isNotBlank(queryString))
                method.setQueryString(URIUtil.encodeQuery(queryString));
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                // response = method.getResponseBodyAsString(); 
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),"utf-8"));
                StringBuffer stringBuffer = new StringBuffer();
                String str = "";
                while ((str = reader.readLine()) != null) {
                    stringBuffer.append(str);
                }
                response = stringBuffer.toString();
                log.info(response);
            }
        } catch (Exception e) {
            log.error("身份认证失败！");
        }finally {
            method.releaseConnection();
        }
        return response;
    }
}