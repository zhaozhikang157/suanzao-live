package com.ll369.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

import com.ll369.console.dao.OrdersMapper;
import com.ll369.model.TransactionBean;

import chinapay.Base64;
import chinapay.PrivateKey;
import chinapay.SecureLink;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:applicationcontext-servlet.xml"})
public class YinlianTest {
	private static Logger log = LoggerFactory.getLogger(YinlianTest.class);
	@Autowired
	OrdersMapper ordersMapper;
    
	/**
	 * 
	* @Title: tixian
	* @Description: TODO(银联提现信息)
	* @return void    返回类型
	 */
	@Test
	public void tixian(){                                 
		TransactionBean pay = SinPayQueryServletGBK("20161108", "2016110816124556");
		String code = pay.getResponseCode();
		if("000".equals(code) && "s".equals(pay.getStat())){
			
			
		}
	}
	
	
	public TransactionBean SinPayQueryServletGBK(String merDate,String merSeqId){
		TransactionBean pay = new TransactionBean();
		try {
			String  path = "classpath:yinlianAssetsTest/dfMerPrK_808080211302864_20151012151533.key";
			String MerKeyPath = ResourceUtils.getFile(path).getPath();

			String  pubpath = "classpath:yinlianAssetsTest/PgPubk.key";
			String PubKeyPath = ResourceUtils.getFile(pubpath).getPath();

			String pay_url = "http://sfj.chinapay.com/dac/SinPayQueryServletGBK";      
			//808080211303632
			
			String merId ="808080211302864";
			String version = "20090501";
			String signFlag = "1";

			String Data = merId + merDate + merSeqId + version;
			log.info("字符串数据拼装结果：" + Data);
			String plainData = new String(Base64.encode(Data.getBytes()));
			log.info("转换成Base64后数据：" + plainData);

			String chkValue = null;
			int KeyUsage = 0;
			PrivateKey key = new PrivateKey();
			key.buildKey(merId, KeyUsage, MerKeyPath);
			SecureLink sl = new SecureLink(key);
			chkValue = sl.Sign(plainData);
			log.info("签名内容:"+ chkValue);

			HttpClient httpClient = new HttpClient();
			log.info("HttpClient方法创建！");
			httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
			String url = pay_url;
			log.info(url);
			PostMethod postMethod = new PostMethod(url);
			log.info("Post方法创建！");
			//填入各个表单域的值
			NameValuePair[] data = { 
					new NameValuePair("merId", merId),
					new NameValuePair("merDate", merDate),
					new NameValuePair("merSeqId", merSeqId),
					new NameValuePair("version", version),
					new NameValuePair("signFlag", signFlag),
					new NameValuePair("chkValue",chkValue)
			};
			System.out.println("merId="+merId);
			System.out.println("merDate="+merDate);
			System.out.println("merSeqId="+merSeqId);
			System.out.println("version="+version);
			System.out.println("signFlag="+signFlag);
			System.out.println("chkValue="+chkValue);

			// 将表单的值放入postMethod中
			postMethod.setRequestBody(data);
			// 执行postMethod
		    httpClient.executeMethod(postMethod);
			// 读取内容
			InputStream resInputStream = null;
			try {
				resInputStream = postMethod.getResponseBodyAsStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 处理内容
			BufferedReader reader = new BufferedReader(new InputStreamReader(resInputStream));
			String tempBf = null;
			StringBuffer html=new StringBuffer(); 
			while((tempBf = reader.readLine()) != null){ 

				html.append(tempBf); 
			}
			String resMes = html.toString();
			log.info("交易接口查询返回报文：" + resMes);
			int dex = resMes.lastIndexOf("|");
			String Res_Code = resMes.substring(0,3);

			//提取返回数据
			if(Res_Code.equals("000")){
				String Res_stat = resMes.substring(dex-2,dex-1);
				String Res_merId = resMes.substring(4,19);
				String Res_merAmt = resMes.substring(20,dex);
				String Res_chkValue = resMes.substring(dex+1);

				log.info("Res_Code=" + Res_Code);
				log.info("Res_merId=" + Res_merId);
				log.info("Res_merAmt=" + Res_merAmt);
				log.info("Res_chkValue=" + Res_chkValue);
				log.info("Res_stat=" + Res_stat);

				String plainData1 = resMes.substring(0,dex+1);
				log.info("需要验签的字段：" + plainData1);
				String plainData2 = new String(Base64.encode(plainData1.getBytes()));
				log.info("转换成Base64后数据：" + plainData2);

				pay.setResponseCode(Res_Code);
				pay.setMerId(Res_merId);
				pay.setMerAmt(Res_merAmt);
				/*pay.setChkValue(Res_chkValue);*/
				pay.setData(resMes);
				pay.setStat(Res_stat);
				

				//对收到的ChinaPay应答传回的域段进行验签
				boolean buildOK = key.buildKey("999999999999999", KeyUsage, PubKeyPath);
				
				if (!buildOK) {
					log.info("银联应答字段验签失败!");
				}
				boolean res=sl.verifyAuthToken(plainData2,Res_chkValue);
				System.out.println(res);
				if (res){
					log.info("验签数据正确!");
				}
				else {
					log.info("签名数据不匹配！");
				}
			}else {
				String Res_chkValue = resMes.substring(dex+1);

				log.info("Res_Code=" + Res_Code);
				log.info("Res_chkValue=" + Res_chkValue);

				String plainData1 = resMes.substring(0,dex+1);
				log.info("需要验签的字段：" + plainData1);
				String plainData2 = new String(Base64.encode(plainData1.getBytes()));
				log.info("转换成Base64后数据：" + plainData2);

				
				pay.setResponseCode(Res_Code);
				pay.setData(resMes);
				

				//对收到的ChinaPay应答传回的域段进行验签
				boolean buildOK = key.buildKey("999999999999999", KeyUsage, PubKeyPath);
				
				if (!buildOK) {
					log.info("银联应答字段验签失败!");
				}
				boolean res=sl.verifyAuthToken(plainData2,Res_chkValue);
				if (res){
					log.info("验签数据正确!");
				}
				else {
					log.info("签名数据不匹配！");
				}
			}

		} catch (Exception e) {
			log.error("", e);
		}
		return pay;
	
	}
	
	   
	
	
	
}
