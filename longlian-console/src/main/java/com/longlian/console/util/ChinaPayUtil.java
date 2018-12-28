package com.longlian.console.util;

import chinapay.PrivateKey;
import chinapay.SecureLink;
import com.huaxin.util.ActResult;
import com.huaxin.util.DateUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.ChinaPayConst;
import com.huaxin.util.constant.Const;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.dto.ChinaPayDto;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.xmlbeans.impl.util.Base64;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by syl on 2016/8/23.
 */
public class ChinaPayUtil {


    public  static  ActResult sendHttpData(ChinaPayDto bankOut , String chkValue , String transAmt)throws  Exception{
        ActResult result = new ActResult();
        //传入显示页面的数据准备
        // TransactionBean pay = new TransactionBean();
        //连接Chinapay控台
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
        String url = ChinaPayConst.DF_PAY_SEND_URL;
        String signFlag = "1";
        PostMethod postMethod = new PostMethod(url);
        //填入各个表单域的值
        NameValuePair[] data = {
                new NameValuePair("merId", bankOut.getMerId()),
                new NameValuePair("merDate", bankOut.getMerDate()),
                new NameValuePair("merSeqId", bankOut.getMerSeqId()),
                new NameValuePair("cardNo", bankOut.getCardNo()),
                new NameValuePair("usrName", bankOut.getUsrName()),
                new NameValuePair("openBank", bankOut.getOpenBank()),
                new NameValuePair("prov", bankOut.getProv()),
                new NameValuePair("city", bankOut.getCity()),
                new NameValuePair("transAmt",transAmt),
                new NameValuePair("purpose", Const.longlian_live_bank_out_defaul_remark ),
                new NameValuePair("subBank",bankOut.getSubBank()),
                new NameValuePair("flag", bankOut.getFlag()),
                new NameValuePair("version", ChinaPayConst.DF_PAY_VERSION),
                new NameValuePair("chkValue",chkValue),
                new NameValuePair("termType",ChinaPayConst.DF_PAY_TERMTYPE),
                new NameValuePair("signFlag", signFlag)
        };
        // 将表单的值放入postMethod中
        postMethod.setRequestBody(data);
        // 执行postMethod
        try {
            httpClient.executeMethod(postMethod);
        } catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 读取内容
        InputStream resInputStream = null;
        try {
            resInputStream = postMethod.getResponseBodyAsStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
       // result.setData(resMes);
        System.out.println(resMes);
        int dex = resMes.lastIndexOf("&");

        //拆分页面应答数据
        String str[] = resMes.split("&");
        //提取返回数据
        if(str.length == 10){
            int Res_Code = str[0].indexOf("=");
            int Res_merId = str[1].indexOf("=");
            int Res_merDate = str[2].indexOf("=");
            int Res_merSeqId = str[3].indexOf("=");
            int Res_cpDate = str[4].indexOf("=");
            int Res_cpSeqId = str[5].indexOf("=");
            int Res_transAmt = str[6].indexOf("=");
            int Res_stat = str[7].indexOf("=");
            int Res_cardNo = str[8].indexOf("=");
            int Res_chkValue = str[9].indexOf("=");
            String responseCode = str[0].substring(Res_Code + 1);
            Map cpMap = new HashMap();
            String CpSeqId = str[5].substring(Res_cpSeqId+1);
            cpMap.put("cpSeqId" ,CpSeqId );
            result.setData(cpMap);
            if(responseCode.equals("0100")){
                result.setSuccess(false);
                result.setMsg("商户提交的字段长度、格式错误！");
                return result;
            }else  if(responseCode.equals("0101")){
                result.setSuccess(false);
                result.setMsg("商户验签错误！");
                return result;
            }else  if(responseCode.equals("0102")){
                result.setSuccess(false);
                result.setMsg("手续费计算出错！");
                return result;
            }else  if(responseCode.equals("0103")){
                result.setSuccess(false);
                result.setMsg("商户备付金帐户金额不足！");
                return result;
            }else  if(responseCode.equals("0104")){
                result.setSuccess(false);
                result.setMsg("操作拒绝！");
                return result;
            }else  if(responseCode.equals("0105")){
                result.setSuccess(false);
                result.setMsg("重复交易！");
                return result;
            }
            String MerId = str[1].substring(Res_merId + 1);
            String MerDate = str[2].substring(Res_merDate + 1);
            String MerSeqId = str[3].substring(Res_merSeqId + 1);
            String CpDate = str[4].substring(Res_cpDate+1);

            String TransAmt = str[6].substring(Res_transAmt+1);
            String Stat = str[7].substring(Res_stat+1);
            String CardNo = str[8].substring(Res_cardNo+1);
            String ChkValue = str[9].substring(Res_chkValue+1);
            String msg = resMes.substring(0, dex);
            String plainData = new String(Base64.encode(msg.getBytes()));

            //对收到的ChinaPay应答传回的域段进行验签
            boolean buildOK = false;
            boolean res = false;
            int KeyUsage = 0;
            PrivateKey key = new PrivateKey();
            if(Stat.equals("6") || Stat.equals("9")){
                result.setSuccess(false);
                result.setMsg("交易失败！");
                return result;
            }else if(Stat.equals("2") || Stat.equals("3")
                    || Stat.equals("4") || Stat.equals("5")
                    || Stat.equals("7") || Stat.equals("8")){
                try {

                    String CHINA_PAY_PGPUBKKEY_FILE = CustomizedPropertyConfigurer.getContextProperty("CHINA_PAY_PGPUBKKEY_FILE");
                    String MerKeyPath = ResourceUtils.getFile(CHINA_PAY_PGPUBKKEY_FILE).getPath();;
                    buildOK = key.buildKey("999999999999999", KeyUsage, MerKeyPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!buildOK) {
                    System.out.println("build error!");
                    result.setSuccess(false);
                    result.setMsg("绑定出错！");
                    return result;
                }
                SecureLink sl = new SecureLink(key);
                res=sl.verifyAuthToken(plainData,ChkValue);
                if (res){
                    System.out.println("验签数据正确!");
                    result.setSuccess(true);
                    result.setCode(Utility.parseInt(Stat));
                    result.setMsg("提交成功,订单正在交易处理中！请查看银联代付状态详情！");
                    return result;
                }
                else {
                    result.setSuccess(false);
                    result.setMsg("签名数据不匹配！");
                    return result;
                }
            }
            try {

                String CHINA_PAY_PGPUBKKEY_FILE = CustomizedPropertyConfigurer.getContextProperty("CHINA_PAY_PGPUBKKEY_FILE");
                String MerKeyPath = ResourceUtils.getFile(CHINA_PAY_PGPUBKKEY_FILE).getPath();;
                buildOK = key.buildKey("999999999999999", KeyUsage, MerKeyPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!buildOK) {
                System.out.println("build error!");
                result.setSuccess(false);
                result.setMsg("绑定出错！");
                return result;
            }
            SecureLink sl = new SecureLink(key);
            res=sl.verifyAuthToken(plainData,ChkValue);
            if (res){
                System.out.println("验签数据正确!");
                result.setSuccess(true);
                result.setMsg("提现成功！");
                return result;
            }
            else {
                result.setSuccess(false);
                result.setMsg("签名数据不匹配！");
                return result;
            }
        }

        //交易失败应答
        if(str.length == 2) {
            int Res_Code = str[0].indexOf("=");
            int Res_chkValue = str[1].indexOf("=");

            String responseCode = str[0].substring(Res_Code + 1);
            if(responseCode.equals("0100")){
                result.setSuccess(false);
                result.setMsg("商户提交的字段长度、格式错误！");
                return result;
            }else  if(responseCode.equals("0101")){
                result.setSuccess(false);
                result.setMsg("商户验签错误！");
                return result;
            }else  if(responseCode.equals("0102")){
                result.setSuccess(false);
                result.setMsg("手续费计算出错！");
                return result;
            }else  if(responseCode.equals("0103")){
                result.setSuccess(false);
                result.setMsg("商户备付金帐户金额不足！");
                return result;
            }else  if(responseCode.equals("0104")){
                result.setSuccess(false);
                result.setMsg("操作拒绝！");
                return result;
            }else  if(responseCode.equals("0105")){
                result.setSuccess(false);
                result.setMsg("重复交易！");
                return result;
            }
            String ChkValue = str[1].substring(Res_chkValue + 1);

            String plainData = str[0];
            String plainData1 = new String(Base64.encode(plainData.getBytes()));

            //对收到的ChinaPay应答传回的域段进行验签
            boolean buildOK = false;
            boolean res = false;
            int KeyUsage = 0;
            PrivateKey key = new PrivateKey();
            try {
                String CHINA_PAY_PGPUBKKEY_FILE = CustomizedPropertyConfigurer.getContextProperty("CHINA_PAY_PGPUBKKEY_FILE");
                String MerKeyPath = ResourceUtils.getFile(CHINA_PAY_PGPUBKKEY_FILE).getPath();;
                buildOK = key.buildKey("999999999999999", KeyUsage,MerKeyPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!buildOK) {
                result.setSuccess(false);
                result.setMsg("绑定出错！");
                return result;
            }
            SecureLink sl = new SecureLink(key);
            res = sl.verifyAuthToken(plainData1, ChkValue);
            System.out.println(res);
            if (res) {
                /*json.setRtState(true);
                json.setRtData(pay);*/
            } else {
                result.setSuccess(false);
                result.setMsg("签名数据不匹配！！");
                return result;
            }
        }
        result.setSuccess(false);
        result.setMsg("提现失败！");
        return  result;
    }




    /**
     * 获取银联提现 商业流水号
     */
    public static String getBankOutMerSeqId(long id){
        String merDate = DateUtil.format(new Date(), "yyyyMMdd");
        String seqIdStr = Utility.getFixLengthNum(id + "", 8);
        String merSeqId = merDate + seqIdStr;
        return  merSeqId;
    }
    /**
     * 获取银联提现 商业流水号  随机
     */
    public static String getBankOutMerSeqId4Random(){
        String merDate = DateUtil.format(new Date(), "yyyyMMdd");
        String seqIdStr =  Utility.getNumCode(0, 8) ;
        String merSeqId = merDate + seqIdStr;
        return  merSeqId;
    }

}
