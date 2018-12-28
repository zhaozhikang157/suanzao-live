/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.huaxin.util.weixin.encryption;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * XMLParse class
 *
 * 提供提取消息格式中的密文及生成回复消息格式的接口.
 */
class XMLParse {

	/**
	 * 提取出xml数据包中的加密消息
	 * @param xmltext 待提取的xml字符串
	 * @return 提取出的加密消息字符串
	 * @throws AesException 
	 */
	public static Object[] extract(String xmltext) throws AesException     {
		Object[] result = new Object[3];
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xmltext);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);
			Element root = document.getDocumentElement();
			System.out.println("root" + root);
		/*	NodeList nodelist1 = root.getElementsByTagName("Encrypt");
			NodeList nodelist2 = root.getElementsByTagName("ToUserName");*/
			org.dom4j.Document document2 = DocumentHelper.parseText(xmltext);
			org.dom4j.Element root2 = document2.getRootElement();
			String Encrypt  = root2.elementText("Encrypt");
			String ToUserName  = root2.elementText("ToUserName");
			System.out.println("root" + root);
			result[0] = 0;
			result[1] = Encrypt;
			result[2] = ToUserName;
			/*if(nodelist2.item(0) != null){
				result[2] = nodelist2.item(0).getTextContent();
			}
			*/
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ParseXmlError);
		}
	}

	public static void main(String[] args) throws Exception{
		String ss = "<xml>" +
				" <AppId><![CDATA[wx3ea560592eab45d8]]></AppId>  " +
				"  <Encrypt><![CDATA[ba7lp8ZCNAvrUWlIaGh8hp7/MuA79NeqYkOp/C+aN9LhFittj8U2SnmqAYe9gK9PAESv99T+QJY3JE+rYYzmrjwlx4N0Y0B9wsr4n3QTqxfvfeYgFp6Tb04eM/uJlvEIU5BX64zMsdm2bk58tDxZuKDMtQJWF7/dyKWROi76A908monBhKtNQ9NwxZptDrX3lhUQN84vlUFIV9Ksu0lrSwXrIfoccusQ/6x8bQWhxjAdolNEkezcHYGOSFRdKYtT83XTeyksce+M4Rab+c2q59DcDwsUr4C/s3mQkGcU4gYvUYVEsWcjuzdkddlvKy7cLR+LDNkcxh/qJUab75LPCHnOGRvy4QFrs2PwtOPmJucVgQK0DIbNjcydhMOwfojqIkUnInOME3wtDbI5uhfczNmnakMb1ypEWfPeaJ69MOnya0GXwRmSO2lXk2aX+iOadKoZJHKB49mXxnbNPUNvhw==]]>" +
				"</Encrypt>" +
				"</xml>";
		Object[]  o =  extract( ss);
		System.out.println(o[1] + "---->"+  o[2]);
	}
	/**
	 * 生成xml消息
	 * @param encrypt 加密后的消息密文
	 * @param signature 安全签名
	 * @param timestamp 时间戳
	 * @param nonce 随机字符串
	 * @return 生成的xml字符串
	 */
	public static String generate(String encrypt, String signature, String timestamp, String nonce) {

		String format = "<xml>\n" + "<Encrypt><![CDATA[%1$s]]></Encrypt>\n"
				+ "<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n"
				+ "<TimeStamp>%3$s</TimeStamp>\n" + "<Nonce><![CDATA[%4$s]]></Nonce>\n" + "</xml>";
		return String.format(format, encrypt, signature, timestamp, nonce);

	}
}
