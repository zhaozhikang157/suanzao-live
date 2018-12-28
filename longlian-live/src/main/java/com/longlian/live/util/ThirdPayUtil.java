package com.longlian.live.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;



public class ThirdPayUtil {
	private static Logger log = LoggerFactory.getLogger(ThirdPayUtil.class);
	
	public static final String ISONLINE = "isOnLine";
	public static final String RESULT = "result";
	
	public static void main(String[] args) throws Exception {
	}
	
	/**
	 * 苹果内购支付 验证
	 * @param Payload
	 * @return map   key:isOnLine 是否是线上测试，result 测试结果
	 * @throws Exception
	 */
	public static Map<String,Boolean> doIosRequest(String Payload) throws Exception {
		System.out.println("客户端传过来的值："+Payload);
		
		Map<String,Boolean> resultMap = new HashMap<>();
		resultMap.put(ISONLINE, true); //是否是线上测试
		resultMap.put(RESULT, false); 
		
	    String verifyResult =  IosVerifyUtil.buyAppVerify(Payload,1); 			//1.先线上测试    发送平台验证 
	    if (verifyResult == null) {   											// 苹果服务器没有返回验证结果         	       
	    	log.info("无订单信息!");
	    } else {  	    														// 苹果验证有返回结果
	    	JSONObject job = JSONObject.parseObject(verifyResult);  
	        String states = job.getString("status");
	        
	        if("21007".equals(states)){											//是沙盒环境，应沙盒测试，否则执行下面	
	        	resultMap.put(ISONLINE, false);
	        	verifyResult =  IosVerifyUtil.buyAppVerify(Payload,0);			//2.再沙盒测试  发送平台验证
	        	job = JSONObject.parseObject(verifyResult);  
		        states = job.getString("status");
	        }
	        log.info("苹果平台返回值：job"+job);
	        if (states.equals("0")){
	        	resultMap.put(RESULT, true);
	        	return resultMap;
	        }   
	    }  
	    return resultMap;
	}  


}
