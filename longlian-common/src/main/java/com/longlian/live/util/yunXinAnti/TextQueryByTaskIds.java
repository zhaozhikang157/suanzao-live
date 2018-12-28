/*
 * @(#) TextCallbackAPIDemo.java 2016年12月28日
 * 
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.longlian.live.util.yunXinAnti;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 调用易盾反垃圾云服务文本结果查询接口API示例，该示例依赖以下jar包：
 * 1. httpclient，用于发送http请求
 * 2. commons-codec，使用md5算法生成签名信息，详细见SignatureUtils.java
 * 3. gson，用于做json解析
 * 
 * @author hzgaomin
 * @version 2016年2月3日
 */
public class TextQueryByTaskIds {
    /** 产品密钥ID，产品标识 */
    private final static String SECRETID = "ce8a2e16e04243d5ca7ae34d6dd50e0e";
    /** 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露 */
    private final static String SECRETKEY = "bf3d5b6956f07c0ca01d5453347966fc";
    /** 业务ID，易盾根据产品业务特点分配 */
    private final static String BUSINESSID = "a4e93d02624d257b821621e9280903f2";
    /** 易盾反垃圾云服务文本在线检测结果获取接口地址 */
    private final static String API_URL = "https://api.aq.163.com/v3/text/check";

    private static Logger log = LoggerFactory.getLogger(TextQueryByTaskIds.class);

    /**
     * 
     * @throws Exception
     */
    public static int Yunxin(String content,String dataId) {
        try {
            log.info("聊天内容 : "+content + " ---- dataId : "+ dataId);
            if (StringUtils.isEmpty(content)) {
                return -1;
            }

            HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 10000, 2000, 2000);
            Map<String, String> params = new HashMap<String, String>();
            // 1.设置公共参数
            params.put("secretId", SECRETID);
            params.put("businessId", BUSINESSID);
            params.put("version", "v3");
            params.put("timestamp", String.valueOf(System.currentTimeMillis()));
            params.put("nonce", String.valueOf(new Random().nextInt()));
            // 2.设置私有参数
            params.put("dataId", dataId);
            params.put("content", content);
            // 3.生成签名信息
            String signature = SignatureUtils.genSignature(SECRETKEY, params);
            params.put("signature", signature);
            // 4.发送HTTP请求，这里使用的是HttpClient工具包，产品可自行选择自己熟悉的工具包发送请求
            String response = HttpClient4Utils.sendPost(httpClient, API_URL, params, Consts.UTF_8);
            // 5.解析接口返回值
            JsonObject resultObject = new JsonParser().parse(response).getAsJsonObject();
            int code = resultObject.get("code").getAsInt();
            if (code == 200) {
                JsonObject resultArray = resultObject.getAsJsonObject("result");
//                for (JsonElement jsonElement : resultArray) {
//                    JsonObject jObject = jsonElement.getAsJsonObject();
                    int action = resultArray.get("action").getAsInt();
                    return action;
//                }
            }
            return -1;
        } catch (Exception e) {
            log.info("错误信息: "+ e);
            return -1 ;
        }
    }
}
