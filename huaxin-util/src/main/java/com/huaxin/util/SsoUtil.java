package com.huaxin.util;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;

/**
 * 阿里云帮助类
 * @author baohj
 *
 */
@Service
public class SsoUtil {
	
	private static Logger log = LoggerFactory.getLogger(SsoUtil.class); 
	
	private static String bucketName="llkeji";
	public static String accessKeyId="HdhrU64EnCRlKEmY";
	public static String accessKeySecret="82lNWjoyAUfTuhXfhcDN4rTWpZM8Ta";
    //上线后，可改为http://oss-cn-beijing-internal.aliyuncs.com/,也可以不改
    private static String endpointWrite="http://oss-cn-beijing.aliyuncs.com/";
    private static String endpointRead="http://oss-cn-beijing.aliyuncs.com/";
	/**
	 * 上传文件
	 * @param yourKey 文件名
	 * @param bytes 文件输入流
	 */
	public String putObject(String yourKey,byte[] bytes){
		OSSClient ossClient = null;
		
		log.info("bucketName:%s,endpoint:%s,accessKeyId:%s,accessKeySecret:%s", bucketName,endpointWrite,accessKeyId,accessKeySecret);
		try {
			ossClient = new OSSClient(endpointWrite,accessKeyId,accessKeySecret);
			//判断bucketName是否存储，如果不存在则创建
			boolean isExist = ossClient.doesBucketExist(bucketName);
			if(!isExist){
				ossClient.createBucket(bucketName);
			}
			ByteArrayInputStream inputstream = new ByteArrayInputStream(bytes);
			ossClient.putObject(bucketName, yourKey, inputstream);
			String url =  getUrl(bucketName, endpointRead, yourKey);
			log.info("save-ali-file-url:%s", url);
			return url;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("上传文件到阿里云异常",e);
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
		return "http://"+bucketName+"."+url+yourKey;
	}


}
