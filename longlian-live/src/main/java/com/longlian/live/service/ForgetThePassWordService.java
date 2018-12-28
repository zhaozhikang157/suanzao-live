package com.longlian.live.service;

import com.longlian.dto.ActResultDto;

import java.util.Optional;

/**
 * Created by wanghuaan on 2018/2/28.
 * 忘记密码相关 业务逻辑
 */
public interface ForgetThePassWordService {

    /**
     * 忘记密码----发送验证码 返回校验结果
     * @param mobile
     * @return
     */
    public ActResultDto forgetThePassWord(String redisKey,Optional<String> mobile);

    /**
     * 个人中心 - 发送验证码 返回校验结果
     * @param redisKey
     * @param mobile
     * @param id
     * @return
     */
    public ActResultDto forgetThePersonalCenter(String redisKey,Optional<String> mobile,Long id);

    /**
     * 忘记密码----校验验证码
     * @param mobile
     * @param code
     * @return
     */
    public ActResultDto verificationCode(Optional<String> mobile,Optional<String> code,AppUserService appUserService,String redisKey);

    /**
     * 设置新密码
     * @param mobile
     * @param password
     * @param appUserService
     * @return
     */
    public ActResultDto setNewPassword(Optional<String> mobile , Optional<String> password,AppUserService appUserService);

    /**
     * 设置新密码或者以及绑定手机号
     * @param mobile
     * @param password
     * @param id
     * @return
     */
    public ActResultDto setNewPasswordByIDAndBd(Optional<String> mobile , Optional<String> password,Long id);


}
