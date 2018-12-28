package com.longlian.live.util;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.huaxin.util.Utility;
import com.longlian.type.OssBucket;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云帮助类
 * @author baohj
 *
 */
@Service("longlianSsoUtil")
public class LonglianSsoUtil {
    
	private static Logger log = LoggerFactory.getLogger(LonglianSsoUtil.class);
	
	//public static String bucketName="longlian-live";
	public static String accessKeyId="HdhrU64EnCRlKEmY";
	public static String accessKeySecret="82lNWjoyAUfTuhXfhcDN4rTWpZM8Ta";

    //上线后，可改为http://oss-cn-beijing-internal.aliyuncs.com/,也可以不改
	//public static String endpointWrite="http://oss-cn-hangzhou.aliyuncs.com/";
	//public static String endpointRead="http://oss-cn-hangzhou.aliyuncs.com/";
	//public static String endpoint = "oss-cn-hangzhou.aliyuncs.com";

	//public static String cdnPoint = "http://file.llkeji.com/";

	//public static Map<String, String> bucketAddress = new HashMap<>();
	//static {
		//bucketAddress.put(bucketName,cdnPoint);
		//bucketAddress.put("longlian-live2","http://longlian-live2.oss-cn-shanghai.aliyuncs.com/");
	//}
	public static OSSClient client = null;

	//private static List<PartETag> partETags = Collections.synchronizedList(new ArrayList<PartETag>());
	static{
		if(client == null){
			client = new OSSClient(OssBucket.longlian_live.getEndpoint(), LonglianSsoUtil.accessKeyId, LonglianSsoUtil.accessKeySecret);
		}
	}
	/**
	 * 上传文件
	 * @param yourKey 文件名
	 * @param bytes 文件输入流
	 */
	public String putObject(String yourKey,byte[] bytes , OssBucket ossBucket){
		OSSClient ossClient = null;
		log.info("bucketName:[{}],endpoint:[{}],accessKeyId:[{}],accessKeySecret:[{}]", ossBucket.getName(),ossBucket.getEndpoint() ,accessKeyId,accessKeySecret);
		try {
			ossClient = new OSSClient(ossBucket.getWriteEndpoint(),accessKeyId,accessKeySecret);
			//判断bucketName是否存储，如果不存在则创建
			boolean isExist = ossClient.doesBucketExist(ossBucket.getName());
			if(!isExist){
				ossClient.createBucket(ossBucket.getName());
			}
			ByteArrayInputStream inputstream = new ByteArrayInputStream(bytes);
			PutObjectResult por = ossClient.putObject(ossBucket.getName(), yourKey, inputstream);
			String url =  getUrl(yourKey , ossBucket);
			log.info("save-ali-file-url:[{}]", url);
			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(ossClient);
		}
		return null;
	}

	public static String getSignUrl(String url) throws ParseException {
		//https://longlian-input.oss-cn-beijing.aliyuncs.com/longlian_test/2017/09/672c05f531ca484aab110818fe8327cb.mp4
		return  url.replace("http://" , "https://");
//		if (StringUtils.isEmpty(url)) {
//			return url;
//		}
//
//		url = url.replace("https://" , "");
//		url = url.replace("http://" , "");
//
//		if (url.indexOf(".") < 0 || url.indexOf("/") < 0) {
//			return url;
//		}
//
//		String bucketName = url.substring(0 , url.indexOf("."));
//		String key = url.substring(url.indexOf("/") + 1);
//
//		OssBucket ossBucket = OssBucket.getOssBucketByName(bucketName);
//		if (ossBucket == null) {
//			ossBucket = OssBucket.longlian_live;
//		}
//
//		//服务器端生成url签名字串
//		OSSClient Server  = new OSSClient(ossBucket.getWriteEndpoint(), accessKeyId, accessKeySecret);
//		Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12);
//		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.GET);
//		//设置过期时间
//		request.setExpiration(expiration);
//		// 生成URL签名(HTTP GET请求)
//		URL signedUrl = Server.generatePresignedUrl(request);
//		//System.out.println("signed url for getObject: " + signedUrl);
//		//客户端使用使用url签名字串发送请求
//		String httpsUrl = signedUrl.toString().replace("http://" , "https://");
//		return signedUrl.toString();
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
	 * @param yourKey
	 * @return
	 */
	public static String getUrl(String yourKey ,OssBucket ossBucket){
		if (ossBucket == null) {
			ossBucket = OssBucket.longlian_live;
		}

		return ossBucket.getBucketHttpsAddress() + getEncodeUrl(yourKey);
	}

	public static String getUrlByName(String yourKey ,String name){
		return getUrl(yourKey , OssBucket.getOssBucketByName(name));
	}

	public static String getEncodeUrl(String str) {
		if (str.endsWith("/")) {
			return str;
		}
		int i = str.lastIndexOf("/");
		String start = str.substring(0 , i+ 1 );
		String end = str.substring(i + 1);
		try {
			return start + URLEncoder.encode(end, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("URL编码出错:{}",str);
			log.error("URL编码出错",e);
			return str;
		}
	}
	/**
	 * 根据key删除OSS服务器上的文件
	 * @param ossClient  oss连接
	 * @param bucketName  存储空间
	 * @param folder  模拟文件夹名 如"qj_nanjing/"
	 * @param key Bucket下的文件的路径名+文件名 如："upload/cake.jpg"
	 */
	public static int deleteFileOfBucket(String bucketName, String folder, String key){
		try{
			client.deleteObject(bucketName, folder + key);
			return 1;
		} catch(Exception e) {
			log.info("删除" + bucketName + "下的文件" + folder + key + "成功");
			e.printStackTrace();
			return 0;
		}
	}
	public static void main(String[] args) {

		String url = "https://longlian-input.oss-cn-beijing.aliyuncs.com/longlian_test/2017/09/672c05f531ca484aab110818fe8327cb.mp4";
		url = url.replace("https://" , "");
		url = url.replace("http://" , "");

		String bucketName = url.substring(0 , url.indexOf("."));
		String key = url.substring(url.indexOf("/") + 1);

		System.out.println(bucketName);
		System.out.println(key);
	}
}
