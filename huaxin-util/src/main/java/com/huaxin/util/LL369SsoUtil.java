package com.huaxin.util;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
/**
 * 阿里云帮助类
 * @author baohj
 *
 */
@Service("ll369SsoUtil")
public class LL369SsoUtil {
    
	private static Logger log = LoggerFactory.getLogger(LL369SsoUtil.class); 
	
	public static String bucketName="ll369-input";
	public static String accessKeyId="HdhrU64EnCRlKEmY";
	public static String accessKeySecret="82lNWjoyAUfTuhXfhcDN4rTWpZM8Ta";
    //上线后，可改为http://oss-cn-beijing-internal.aliyuncs.com/,也可以不改
	public static String endpointWrite="http://oss-cn-hangzhou.aliyuncs.com/";
	public static String endpointRead="http://oss-cn-hangzhou.aliyuncs.com/";
	/**
	 * 上传文件
	 * @param yourKey 文件名
	 * @param bytes 文件输入流
	 */
	public String putObject(String yourKey,byte[] bytes){
		OSSClient ossClient = null;
		log.info("bucketName:[{}],endpoint:[{}],accessKeyId:[{}],accessKeySecret:[{}]", bucketName,endpointWrite,accessKeyId,accessKeySecret);
		try {
			ossClient = new OSSClient(endpointWrite,accessKeyId,accessKeySecret);
			//判断bucketName是否存储，如果不存在则创建
			boolean isExist = ossClient.doesBucketExist(bucketName);
			if(!isExist){
				ossClient.createBucket(bucketName);
			}
			ByteArrayInputStream inputstream = new ByteArrayInputStream(bytes);
			PutObjectResult por = ossClient.putObject(bucketName, yourKey, inputstream);
			String url =  getUrl(bucketName, endpointRead, yourKey);
			log.info("save-ali-file-url:[{}]", url);
			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(ossClient);
		}
		return null;
	}
	
	/**
	 * 关闭客户端
	 * @param ossClient
	 */
	public void close(OSSClient ossClient){
		if(ossClient != null){
			ossClient.shutdown();
		}
	}
	
	/**
	 * 组装阿里服务器图片地址
	 * @param bucketName
	 * @param endpoint
	 * @param yourKey
	 * @return
	 */
	public static String getUrl(String bucketName,String endpoint,String yourKey){
		int index = endpoint.indexOf("//");
		String url = endpoint.substring(index+2);
		//转为https
		return "https://"+bucketName+"."+url+yourKey;
	}

	

}
