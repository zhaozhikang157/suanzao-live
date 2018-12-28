package com.huaxin.util.weixin.encryption;

import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.weixin.oauth2.ThirdOAuth2;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class WXBizMsgCryptTest {
	String encodingAesKey = "df4frrrrrr666666666666666666666666666666666";
	String token = "test";
	String timestamp = "1409304348";
	String nonce = "xxxxxx";
	String appId = "wx3ea560592eab45d8";
	String replyMsg = "我是中文abcd123";
	String xmlFormat = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
	String afterAesEncrypt = "jn1L23DB+6ELqJ+6bruv21Y6MD7KeIfP82D6gU39rmkgczbWwt5+3bnyg5K55bgVtVzd832WzZGMhkP72vVOfg==";
	String randomStr = "aaaabbbbccccdddd";

	String replyMsg2 = "<xml><ToUserName><![CDATA[oia2Tj我是中文jewbmiOUlr6X-1crbLOvLw]]></ToUserName><FromUserName><![CDATA[gh_7f083739789a]]></FromUserName><CreateTime>1407743423</CreateTime><MsgType><![CDATA[video]]></MsgType><Video><MediaId><![CDATA[eYJ1MbwPRJtOvIEabaxHs7TX2D-HV71s79GUxqdUkjm6Gs2Ed1KF3ulAOA9H1xG0]]></MediaId><Title><![CDATA[testCallBackReplyVideo]]></Title><Description><![CDATA[testCallBackReplyVideo]]></Description></Video></xml>";
	String afterAesEncrypt2 = "jn1L23DB+6ELqJ+6bruv23M2GmYfkv0xBh2h+XTBOKVKcgDFHle6gqcZ1cZrk3e1qjPQ1F4RsLWzQRG9udbKWesxlkupqcEcW7ZQweImX9+wLMa0GaUzpkycA8+IamDBxn5loLgZpnS7fVAbExOkK5DYHBmv5tptA9tklE/fTIILHR8HLXa5nQvFb3tYPKAlHF3rtTeayNf0QuM+UW/wM9enGIDIJHF7CLHiDNAYxr+r+OrJCmPQyTy8cVWlu9iSvOHPT/77bZqJucQHQ04sq7KZI27OcqpQNSto2OdHCoTccjggX5Z9Mma0nMJBU+jLKJ38YB1fBIz+vBzsYjrTmFQ44YfeEuZ+xRTQwr92vhA9OxchWVINGC50qE/6lmkwWTwGX9wtQpsJKhP+oS7rvTY8+VdzETdfakjkwQ5/Xka042OlUb1/slTwo4RscuQ+RdxSGvDahxAJ6+EAjLt9d8igHngxIbf6YyqqROxuxqIeIch3CssH/LqRs+iAcILvApYZckqmA7FNERspKA5f8GoJ9sv8xmGvZ9Yrf57cExWtnX8aCMMaBropU/1k+hKP5LVdzbWCG0hGwx/dQudYR/eXp3P0XxjlFiy+9DMlaFExWUZQDajPkdPrEeOwofJb";
	private final static String COMPONENT_APPID = "wx3ea560592eab45d8";
	private final String COMPONENT_APPSECRET = "518d5ccf47a8f9a9f25e00f43f10d241";
	private final static String COMPONENT_ENCODINGAESKEY = "df4frrrrrr666666666666666666666666666666666";
	private final static String COMPONENT_TOKEN = "test";
	public void testNormal() throws ParserConfigurationException, SAXException, IOException,Exception{
		try {
			WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
		/*	String afterEncrpt = pc.encryptMsg(replyMsg, timestamp, nonce);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(afterEncrpt);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("Encrypt");
			NodeList nodelist2 = root.getElementsByTagName("MsgSignature");

			String encrypt = nodelist1.item(0).getTextContent();
			String msgSignature = nodelist2.item(0).getTextContent();
			String fromXML = String.format(xmlFormat, encrypt);*/


			//token:testtimeStamp:1485063872encrypt:hgX91j/1oMb2dWbF8R60Ky91igIbszmElPSgQP/F2OzGbClNA1VzCkULV+WXWfPJtchkY+BQdWkWcN58xVKoN4VZ+iu5np94TCKmqIv+K1UmIeSMhx2Et0t55krAr9lZFLQ+R1CG2++yXeSUqYxC3pMAD5BGIDS39acIwDgKvr+xy8Fh7oXEHiSKXLTjmzuvglxQx1z113R1psL9GXMpre3vtE9O8+UiKtXRw2hQJAWARN++aOOTdWMuJd/fYNZR2CrPIgRM7Cg4D9oOzMk7j9GGWISSb6hhdNrhvZjPExkweIENAZQCc2m09SjbLeR9uLQ+X+XdX4L1rCcNCKWv46/03nUq2hQpZkKL+Br/cqTOGWqGRTMbEpfHkFZn3qh9gAwTiiJlaL0Xh2jBZxSsMpOYCDxthmXPiDUtZb9Xcp6JF+07yJKxZC+ZCFPQfy0NOiRCFlFskhcyX4PJaQGUJw==

			String ss = "<xml><AppId><![CDATA[wx3ea560592eab45d8]]></AppId>    <Encrypt><![CDATA[hgX91j/1oMb2dWbF8R60Ky91igIbszmElPSgQP/F2OzGbClNA1VzCkULV+WXWfPJtchkY+BQdWkWcN58xVKoN4VZ+iu5np94TCKmqIv+K1UmIeSMhx2Et0t55krAr9lZFLQ+R1CG2++yXeSUqYxC3pMAD5BGIDS39acIwDgKvr+xy8Fh7oXEHiSKXLTjmzuvglxQx1z113R1psL9GXMpre3vtE9O8+UiKtXRw2hQJAWARN++aOOTdWMuJd/fYNZR2CrPIgRM7Cg4D9oOzMk7j9GGWISSb6hhdNrhvZjPExkweIENAZQCc2m09SjbLeR9uLQ+X+XdX4L1rCcNCKWv46/03nUq2hQpZkKL+Br/cqTOGWqGRTMbEpfHkFZn3qh9gAwTiiJlaL0Xh2jBZxSsMpOYCDxthmXPiDUtZb9Xcp6JF+07yJKxZC+ZCFPQfy0NOiRCFlFskhcyX4PJaQGUJw==]]></Encrypt></xml>";
					// 第三方收到公众号平台发送的消息
			String afterDecrpt = pc.decryptMsg("4e34db5d629136f2eb453d58b4a79a0db78ad27d", "1485063872", "213044527", ss);

			org.dom4j.Document doc;
			try {
				doc = DocumentHelper.parseText(afterDecrpt);
				org.dom4j.Element rootElt = doc.getRootElement();
				String ticket = rootElt.elementText("ComponentVerifyTicket");
				System.out.println("ticket---------->" + ticket);
				if(!Utility.isNullorEmpty(ticket)){
					ticket = "ticket@@@c9K10L1Jv3VFWD3hTjLEv3B2GhvqEy3dSzthwm6Am0bHLIk3hteM4wuNNJ_fjz4ofWiLVEfXzL3bLJ5TtTS1uw";
					ThirdOAuth2.getThirdComponentAccessToken(COMPONENT_APPID , COMPONENT_APPSECRET ,ticket );
					//redisUtil.set(RedisKey.ll_live_component_verify_ticket ,ticket );
				}
			} catch (DocumentException e) {
				e.printStackTrace();
			}

		} catch (AesException e) {
		}
	}

	public static void main(String[] args) throws Exception{
		WXBizMsgCryptTest wxBizMsgCryptTest = new WXBizMsgCryptTest();
		wxBizMsgCryptTest.testNormal();
	}
	public void testAesEncrypt() {
		try {
			WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
		} catch (AesException e) {
			e.printStackTrace();
		}
	}

	public void testAesEncrypt2() {
		try {
			WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);

		} catch (AesException e) {
			e.printStackTrace();
		}
	}

	public void testIllegalAesKey() {
		try {
			new WXBizMsgCrypt(token, "abcde", appId);
		} catch (AesException e) {
			return;
		}
	}

	public void testValidateSignatureError() throws ParserConfigurationException, SAXException,
			IOException {
		try {
			WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
			String afterEncrpt = pc.encryptMsg(replyMsg, timestamp, nonce);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(afterEncrpt);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("Encrypt");

			String encrypt = nodelist1.item(0).getTextContent();
			String fromXML = String.format(xmlFormat, encrypt);
			pc.decryptMsg("12345", timestamp, nonce, fromXML); // 这里签名错误
		} catch (AesException e) {
			return;
		}
	}
	public void testVerifyUrl() throws AesException {
		WXBizMsgCrypt wxcpt = new WXBizMsgCrypt("QDG6eK",
				"jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C", "wx5823bf96d3bd56c7");
		String verifyMsgSig = "5c45ff5e21c57e6ad56bac8758b79b1d9ac89fd3";
		String timeStamp = "1409659589";
		String nonce = "263014780";
		String echoStr = "P9nAzCzyDtyTWESHep1vC5X9xho/qYX3Zpb4yKa9SKld1DsH3Iyt3tP3zNdtp+4RPcs8TgAE7OaBO+FZXvnaqQ==";
		wxcpt.verifyUrl(verifyMsgSig, timeStamp, nonce, echoStr);
		// 只要不抛出异常就好
	}
}
