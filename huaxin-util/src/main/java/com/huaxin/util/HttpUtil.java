package com.huaxin.util;


import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: HttpPostCommon
 * @Description: TODO(接口调用)
 * @author baohj
 * @date 2014年11月28日 下午1:30:09
 */
public class HttpUtil {

	private static Logger log = LoggerFactory.getLogger(HttpUtil.class);  

	public static String doPost(String url, String body) throws Exception {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
		log.info("@request url:"+url);
		log.info("@request body:"+body);
		HttpPost httpPost = new HttpPost(url);  
		StringEntity entity = null;  
		String result = null;
		try {  
			entity = new StringEntity(body, "utf-8");  
			httpPost.setEntity(entity);  
			HttpResponse httpResponse;  
			httpResponse = closeableHttpClient.execute(httpPost); 
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			log.info("@response statusCode:"+statusCode);
			if (statusCode == 200) {  
				HttpEntity httpEntity = httpResponse.getEntity();  
				 result = EntityUtils.toString(httpEntity, "UTF-8");  
				log.info("@response result:"+result);
			}
		} catch (Exception e) {  
			log.error("", e);
		}finally {
			if(closeableHttpClient != null){
				closeableHttpClient.close();
			}
		}
		return result;
	}
}
