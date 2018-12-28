package com.longlian.live.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.MessageClient;
import com.huaxin.util.Utility;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.security.MD5PassEncrypt;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.AppUserMapper;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.service.AppUserService;
import com.longlian.live.service.ForgetThePassWordService;
import com.longlian.live.util.ShortMessage;
import com.longlian.model.AppUser;
import com.longlian.type.ReturnMessageType;
import com.longlian.type.SMSTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wanghuaan on 2018/2/28.
 * 忘记密码相关 业务逻辑
 */
@Service("forgetThePassWordService")
public class ForgetThePassWordServiceImpl implements ForgetThePassWordService {

    private static Logger log = LoggerFactory.getLogger(ForgetThePassWordServiceImpl.class);

    @Autowired
    private AppUserCommonService appUserCommonService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MessageClient messageClient;

    @Autowired
    AppUserMapper appUserMapper;

    @Autowired
    ShortMessage shortMessage;
    /**
     * 忘记密码----发送验证码 返回校验结果
     * @param mobile
     * @return
     */
    @Override
    public ActResultDto forgetThePassWord(String redisKey,Optional<String> mobile) {
        ActResultDto result = new ActResultDto();//创建返回模型对象
        if(!mobile.isPresent()){//判断手机号是否为空
            log.info("================手机号码输入为空====================");
            result.setCode(ReturnMessageType.CODE_MOBILE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_FALSE.getMessage());
            return result;
        }
        if (!isMobileNO(mobile.get())) {//判断是否为手机号码
            log.info("================输入的手机号非法:"+mobile.get());
            result.setCode(ReturnMessageType.CODE_MOBILE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_FALSE.getMessage());
            return result;
        }
        AppUser selectUser =appUserCommonService.queryByMobile(mobile.get());//查询平台用户
        if(selectUser == null){
            result.setCode(ReturnMessageType.NOT_EXIT_APP_USER.getCode());
            result.setMessage(ReturnMessageType.NOT_EXIT_APP_USER.getMessage());
            return result;
        }
        if("1".equals(selectUser.getStatus())){
            result.setCode(ReturnMessageType.USER_NOT_LOGIN.getCode());
            result.setMessage(ReturnMessageType.USER_NOT_LOGIN.getMessage());
            return result;
        }
        //密码为空,不能发送验证码
        if(StringUtils.isEmpty(selectUser.getPassword())){
            result.setCode(ReturnMessageType.NO_PASSWORD_NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_PASSWORD_NO_LOGIN.getMessage());
            return result;
        }
       String number = redisUtil.get(redisKey + mobile);//从redis获取
        if (Utility.isNullorEmpty(number)){
            number = getCheckCode();//如果没有随机生成6位
        }
        String content = "您的验证码为:" + number + "请在有效期2分钟内验证,注意安全！【酸枣在线】";
        log.info("================验证码获取成功-手机号：" + mobile.get() + "-验证码:" + number);
        //messageClient.sendMessage(mobile.get(), content);
        shortMessage.send_message2(Optional.of(SMSTemplate.SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD.getName()),
                Optional.of(SMSTemplate.SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD.getCode()), mobile,number);
        result.setExt(mobile.get());
        result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        redisUtil.set(redisKey + mobile.get(), number);          //存放到redis
        redisUtil.expire(redisKey + mobile.get(), 2 * 60);     //设置验证码有效期2分钟
        return result;
    }

    @Override
    public ActResultDto verificationCode(Optional<String> mobile, Optional<String> code,AppUserService appUserService,String redisKey) {
        ActResultDto result = new ActResultDto();//创建返回模型对象
        if(!mobile.isPresent()){//判断手机号是否为空
            log.info("================手机号码输入为空====================");
            result.setCode(ReturnMessageType.CODE_MOBILE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_FALSE.getMessage());
            return result;
        }
        if (!isMobileNO(mobile.get())) {//判断是否为手机号码
            log.info("================输入的手机号非法:"+mobile.get());
            result.setCode(ReturnMessageType.CODE_MOBILE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_FALSE.getMessage());
            return result;
        }
        if(!code.isPresent()){//验证码为空
            log.info("================验证码输入为空====================");
            result.setCode(ReturnMessageType.CODE_VERIFICATION_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_VERIFICATION_TRUE.getMessage());
            return result;
        }
        Boolean codeStatus = appUserService.verificationCode(mobile.get(), code.get(), redisKey);
        if(!codeStatus){
            log.info("================验证码错误或已经超时====================");
            result.setCode(ReturnMessageType.CODE_VERIFICATION_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_VERIFICATION_TRUE.getMessage());
            return result;
        }
        result.setExt(mobile.get());
        result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return result;
    }

    /**
     * 设置新密码
     * @param mobile
     * @return
     */
    @Override
    public ActResultDto setNewPassword(Optional<String> mobile , Optional<String> password,AppUserService appUserService){
        if(!mobile.isPresent()||!password.isPresent()){
            log.info("================手机号或者密码丢失====================");
            ActResultDto result = new ActResultDto();
            result.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        String md5 = MD5PassEncrypt.getMD5Str(password.get());
        return appUserService.setPassword(mobile.get(),md5);
    }

    /**
     * 个人中心 - 发送验证码 返回校验结果
     * @param redisKey
     * @param mobile
     * @param id
     * @return
     */
    @Override
    public ActResultDto forgetThePersonalCenter(String redisKey, Optional<String> mobile, Long id) {
        ActResultDto result = new ActResultDto();//创建返回模型对象
        if(!mobile.isPresent()){//判断手机号是否为空
            log.info("================手机号码输入为空====================");
            result.setCode(ReturnMessageType.CODE_MOBILE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_FALSE.getMessage());
            return result;
        }
        if (!isMobileNO(mobile.get())) {//判断是否为手机号码
            log.info("================输入的手机号非法:"+mobile.get());
            result.setCode(ReturnMessageType.CODE_MOBILE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_FALSE.getMessage());
            return result;
        }
        AppUser selectUser =appUserCommonService.queryByMobile(mobile.get());//查询平台用户
        if(selectUser != null&&selectUser.getId()!=id){
            result.setCode(ReturnMessageType.CODE_MOBILE_ISEXIST_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_ISEXIST_TRUE.getMessage());
            return result;
        }
        String number = redisUtil.get(redisKey + mobile);//从redis获取
        if (Utility.isNullorEmpty(number)){
            number = getCheckCode();//如果没有随机生成6位
        }
        String content = "您的验证码为:" + number + "请在有效期2分钟内验证,注意安全！【酸枣在线】";
        log.info("================验证码获取成功-手机号：" + mobile.get() + "-验证码:" + number);
        //messageClient.sendMessage(mobile.get(), content);
        shortMessage.send_message2(Optional.of(SMSTemplate.SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD.getName()),
                Optional.of(SMSTemplate.SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD.getCode()), mobile,number);
        result.setExt(mobile.get());
        result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        redisUtil.set(redisKey + mobile.get(), number);          //存放到redis
        redisUtil.expire(redisKey + mobile.get(), 2 * 60);     //设置验证码有效期2分钟
        return result;
    }

    /**
     * 设置新密码或者以及绑定手机号
     * @param mobile
     * @param password
     * @param id
     * @return
     */
    @Override
    public ActResultDto setNewPasswordByIDAndBd(Optional<String> mobile, Optional<String> password,Long id) {
        ActResultDto result = new ActResultDto();
        if(!mobile.isPresent()||!password.isPresent()){
            log.info("================手机号或者密码丢失====================");
            result.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        String md5 = MD5PassEncrypt.getMD5Str(password.get());
        appUserMapper.updateMobileAndPassword(mobile.get(),md5,id);
        return result;
    }

    /**
     * 判断是否是手机号
     *
     * @param mobiles
     * @return
     */
    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((16[0-9])|(19[0-9])|(17[0-9])|(14[0-9])|(13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    // 产生6位长度的验证码
    public String getCheckCode() {
        String checkCode = "";
        for (int i = 0; i < 6; i++) {
            String code = (int) Math.floor(Math.random() * 10) + "";
            checkCode += code;
        }
        return checkCode;
    }
}
