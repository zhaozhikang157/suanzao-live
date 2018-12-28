package com.longlian.live.util;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.huaxin.util.ActResult;
import com.huaxin.util.MessageClient;
import com.huaxin.util.StringUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by admin on 2018/3/13.
 * 阿里短信接口
 */
@Service("shortMessage")
public class ShortMessage {
    @Autowired
    private MessageClient messageClient;
    //日志输出
    private static Logger log = LoggerFactory.getLogger(ShortMessage.class);
    //初始化ascClient需要的几个参数
    final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
    final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
    final String accessKeyId = "HdhrU64EnCRlKEmY";//你的accessKeyId,参考本文档步骤2
    final String accessKeySecret = "82lNWjoyAUfTuhXfhcDN4rTWpZM8Ta";//你的accessKeySecret，参考本文档步骤2

    /**
     * 短信发送接口
     * @param signName  短信签名 阿里优先
     * @param templateCode 短信模板ID
     * @param phone 手机号
     * @return
     */
    public void send_message(Optional<String> signName,Optional<String> templateCode,Optional<String> phone,String number) {
        //参数完整性验证
        if(!signName.isPresent()){
            log.info("参数丢失：短信签名");
        }
        if(!templateCode.isPresent()){
            log.info("参数丢失：短信模板ID");
        }
        if(!phone.isPresent()){
            log.info("参数丢失：手机号");
        }
        log.info("用户注册短信验证码发送开始 短信签名:"+signName.get()+" 短信模板ID:"+templateCode.get()+" 手机号:"+phone.get());
        try {
            //设置超时时间-可自行调整
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            // 使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，
            // 批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,
            // 验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumbers(phone.get());
            // 必填:短信签名-可在短信控制台中找到
            request.setSignName(signName.get());
            // 必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode.get());
            // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            // 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParam("{\"code\":\""+number+"\"}");
            // 请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                log.info("短信验证码发送成功");
            }else{
                log.info("阿里云短信验证码发送失败:"+sendSmsResponse.getMessage());
                String content = "您的验证码为:" + number + "请在有效期2分钟内验证,注意安全！【酸枣在线】";
                messageClient.sendMessage(phone.get(), content);
                log.info("第三方短信验证码发送成功");
            }
        }catch (Exception e){
            log.error("send msgcode error :{}" + e.getMessage(), e);
        }
    }

    /**
     * 短信发送接口
     * @param signName  短信签名 阿里备用
     * @param templateCode 短信模板ID
     * @param phone 手机号
     * @return
     */
    public void send_message2(Optional<String> signName,Optional<String> templateCode,Optional<String> phone,String number) {
        //参数完整性验证
        if(!signName.isPresent()){
            log.info("参数丢失：短信签名");
        }
        if(!templateCode.isPresent()){
            log.info("参数丢失：短信模板ID");
        }
        if(!phone.isPresent()){
            log.info("参数丢失：手机号");
        }
        log.info("用户注册短信验证码发送开始 短信签名:"+signName.get()+" 短信模板ID:"+templateCode.get()+" 手机号:"+phone.get());
        try {
            String content = "您的验证码为:" + number + "请在有效期2分钟内验证,注意安全！【酸枣在线】";
            String s = messageClient.sendMessage(phone.get(), content);
            if(!StringUtils.isEmpty(s)){
                log.info("第三方短信验证码发送成功");
            }else{

                //设置超时时间-可自行调整
                System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
                System.setProperty("sun.net.client.defaultReadTimeout", "10000");
                //初始化ascClient,暂时不支持多region（请勿修改）
                IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
                DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
                IAcsClient acsClient = new DefaultAcsClient(profile);
                //组装请求对象
                SendSmsRequest request = new SendSmsRequest();
                // 使用post提交
                request.setMethod(MethodType.POST);
                //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，
                // 批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,
                // 验证码类型的短信推荐使用单条调用的方式
                request.setPhoneNumbers(phone.get());
                // 必填:短信签名-可在短信控制台中找到
                request.setSignName(signName.get());
                // 必填:短信模板-可在短信控制台中找到
                request.setTemplateCode(templateCode.get());
                // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
                // 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
                request.setTemplateParam("{\"code\":\""+number+"\"}");
                // 请求失败这里会抛ClientException异常
                SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
                if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                    log.info("短信验证码发送成功");
                }else{
                    log.info("阿里云短信验证码发送失败:"+sendSmsResponse.getMessage());
                }
            }
        }catch (Exception e){
            log.error("send msgcode error :{}" + e.getMessage(), e);
        }
    }
}
