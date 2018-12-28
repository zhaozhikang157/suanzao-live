package com.huaxin.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import com.huaxin.util.dto.XmlDto;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by Administrator on 2015/10/20.
 */
@Component
public class MessageClient {

    private String url;

    private String userId;

    private String account;

    private String password;

    private String extNo;

    private String sendTime = Utility.getDateTimeStr(new Date());


    //发送短信
    public String sendMessage(String mobile, String content) {
        url=CustomizedPropertyConfigurer.getContextProperty("message.url");
        userId=CustomizedPropertyConfigurer.getContextProperty("message.userId");
        account=CustomizedPropertyConfigurer.getContextProperty("message.account");
        password=CustomizedPropertyConfigurer.getContextProperty("message.password");
        extNo=CustomizedPropertyConfigurer.getContextProperty("message.extend.no");
        try {
            String send_content = URLEncoder.encode(content.replaceAll("<br/>", " "), "UTF-8");//发送内容
            String verUrl = url + "?action=send&userid=" + userId + "&account=" + account + "&password=" + password + "&mobile=" + mobile + "&content=" + send_content + "&sendTime=" + sendTime + "&extno=" + extNo;
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(verUrl).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                System.out.println(result);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //查询短信余额
    public String SelSum()  {
        try {
            String verUrl = "http://dx.ipyy.net/sms.aspx?action=overage&userid=&account=xd0111&password=5t4r3e2w1q" ;
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(verUrl).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                System.out.println(result);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //解析xml字符串
    public void readStringXml(String xml) {
        Document doc = null;

        try {
            //将字符转化为XML
            doc = DocumentHelper.parseText(xml);
            //获取根节点
            Element rootElt = doc.getRootElement();

            //拿到根节点的名称
            //System.out.println("根节点名称："+rootElt.getName());

            //获取根节点下的子节点的值
            String returnstatus = rootElt.elementText("returnstatus").trim();
            String message = rootElt.elementText("message").trim();
            String payinfo = rootElt.elementText("payinfo").trim();
            String overage = rootElt.elementText("overage").trim();
            String sendTotal = rootElt.elementText("sendTotal").trim();

            System.out.println("返回状态为：" + returnstatus);
            System.out.println("返回信息提示：" + message);
            System.out.println("返回支付方式：" + payinfo);
            System.out.println("返回剩余短信条数：" + overage);
            System.out.println("返回总条数：" + sendTotal);

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    //XML字符串解析通用方法
    public XmlDto readStringXmlCommen(XmlDto xmlentity, String xml) {
        XmlDto xmlDto = new XmlDto();

        Document document = null;

        try {
            //将字符转化为XML
            document = DocumentHelper.parseText(xml);
            //获取根节点
            Element rootElt = document.getRootElement();
            //拿到根节点的名称
            //System.out.println("根节点：" + rootElt.getName());

            //获取根节点下的子节点的值
            if (xmlentity.getReturnstatus() != null) {
                xmlDto.setReturnstatus(rootElt.elementText(xmlentity.getReturnstatus()).trim());
            }
            if (xmlentity.getMessage() != null) {
                xmlDto.setMessage(rootElt.elementText(xmlentity.getMessage()).trim());
            }
            if (xmlentity.getRemainpoint() != null) {
                xmlDto.setRemainpoint(rootElt.elementText(xmlentity.getRemainpoint()).trim());
            }
            if (xmlentity.getTaskID() != null) {
                xmlDto.setTaskID(rootElt.elementText(xmlentity.getTaskID()).trim());
            }
            if (xmlentity.getSuccessCounts() != null) {
                xmlDto.setSuccessCounts(rootElt.elementText(xmlentity.getSuccessCounts()).trim());
            }
            if (xmlentity.getPayinfo() != null) {
                xmlDto.setPayinfo(rootElt.elementText(xmlentity.getPayinfo()).trim());
            }
            if (xmlentity.getOverage() != null) {
                xmlDto.setOverage(rootElt.elementText(xmlentity.getOverage()).trim());
            }
            if (xmlentity.getSendTotal() != null) {
                xmlDto.setSendTotal(rootElt.elementText(xmlentity.getSendTotal()).trim());
            }
            //接收状态返回的报告
            if (rootElt.hasMixedContent() == false) {
                System.out.println("无返回状态！");
            } else {
                for (int i = 1; i <= rootElt.elements().size(); i++) {
                    if (xmlentity.getStatusbox() != null) {
                        System.out.println("状态" + i + ":");
                        //获取根节点下的子节点statusbox
                        Iterator iter = rootElt.elementIterator(xmlentity.getStatusbox());
                        // 遍历statusbox节点
                        while (iter.hasNext()) {
                            Element recordEle = (Element) iter.next();
                            xmlDto.setMobile(recordEle.elementText("mobile").trim());
                            xmlDto.setTaskid(recordEle.elementText("taskid").trim());
                            xmlDto.setStatus(recordEle.elementText("status").trim());
                            xmlDto.setReceivetime(recordEle.elementText("receivetime").trim());
                            System.out.println("对应手机号：" + xmlDto.getMobile());
                            System.out.println("同一批任务ID：" + xmlDto.getTaskid());
                            System.out.println("状态报告----10：发送成功，20：发送失败：" + xmlDto.getStatus());
                            System.out.println("接收时间：" + xmlDto.getReceivetime());
                        }
                    }

                }

            }

            //错误返回的报告
            if (xmlentity.getErrorstatus() != null) {
                //获取根节点下的子节点errorstatus
                Iterator itererr = rootElt.elementIterator(xmlentity.getErrorstatus());
                // 遍历errorstatus节点
                while (itererr.hasNext()) {
                    Element recordElerr = (Element) itererr.next();
                    xmlDto.setError(recordElerr.elementText("error").trim());
                    xmlDto.setRemark(recordElerr.elementText("remark").trim());
                    System.out.println("错误代码：" + xmlDto.getError());
                    System.out.println("错误描述：" + xmlDto.getRemark());
                }
            }

//			if(xmlentity.getCallbox()!=null)
//			{
//				//获取根节点下的子节点errorstatus
//				Iterator itercallbox = rootElt.elementIterator("errorstatus");
//				// 遍历errorstatus节点
//				while(itercallbox.hasNext())
//				{
//					Element recordcallbox = (Element) itercallbox.next();
//					String content=recordcallbox.elementText("content").trim();
//					String receivetime=recordcallbox.elementText("receivetime").trim();
//					String mobile=recordcallbox.elementText("mobile").trim();
//					String taskid=recordcallbox.elementText("taskid").trim();
//
//				}
//			}

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return xmlDto;
    }

}
