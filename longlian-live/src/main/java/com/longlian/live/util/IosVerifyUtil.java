package com.longlian.live.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
  
/** 
 * 苹果IAP内购验证工具类
 * @ClassName: IosVerify 
 * @Description:Apple Pay 
 */  
public class IosVerifyUtil {  
	private static Logger log = LoggerFactory.getLogger(IosVerifyUtil.class);
  
    private static class TrustAnyTrustManager implements X509TrustManager {  
  
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
        }  
  
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
        }  
  
        public X509Certificate[] getAcceptedIssuers() {  
            return new X509Certificate[] {};  
        }  
    }  
  
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {  
        public boolean verify(String hostname, SSLSession session) {  
            return true;  
        }  
    }  
  
    private static final String url_sandbox = "https://sandbox.itunes.apple.com/verifyReceipt";  
    private static final String url_verify = "https://buy.itunes.apple.com/verifyReceipt";  
  
    /** 
     * 苹果服务器验证 
     *  
     * @param receipt 
     *            账单 
     * @url 要验证的地址 
     * @return null 或返回结果 沙盒 https://sandbox.itunes.apple.com/verifyReceipt 
     *  
     */  
    public static String buyAppVerify(String receipt,int type) {  
    	//环境判断 线上/开发环境用不同的请求链接  
    	String url = "";
    	if(type==0){
    		url = url_sandbox; //沙盒测试
    	}else{
    		url = url_verify; //线上测试
    	}
        //String url = EnvUtils.isOnline() ?url_verify : url_sandbox;  
    	
        try {  
            SSLContext sc = SSLContext.getInstance("SSL");  
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());  
            URL console = new URL(url);  
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();  
            conn.setSSLSocketFactory(sc.getSocketFactory());  
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());  
            conn.setRequestMethod("POST");  
            conn.setRequestProperty("content-type", "text/json");  
            conn.setRequestProperty("Proxy-Connection", "Keep-Alive");  
            conn.setDoInput(true);  
            conn.setDoOutput(true);  
            BufferedOutputStream hurlBufOus = new BufferedOutputStream(conn.getOutputStream());  
  
            String str = String.format(Locale.CHINA, "{\"receipt-data\":\"" + receipt + "\"}");//拼成固定的格式传给平台
            hurlBufOus.write(str.getBytes());  
            hurlBufOus.flush();  
  
            InputStream is = conn.getInputStream();  
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));  
            String line = null;  
            StringBuffer sb = new StringBuffer();  
            while ((line = reader.readLine()) != null) {  
                sb.append(line);  
            }  
  
            return sb.toString();  
        } catch (Exception ex) {  
        	log.error("苹果服务器异常",ex);
//        	System.out.println("苹果服务器异常");
//            ex.printStackTrace();  
        }  
        return null;  
    }  
    
} 
