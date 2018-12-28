package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AppUserService;
import com.longlian.live.service.ForgetThePassWordService;
import com.longlian.token.AppUserIdentity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by wanghuaan on 2018/2/28.
 * 忘记密码相关 控制层接口
 */
@RequestMapping("/forget")
@Controller
public class ForgetThePassWordController {

    @Autowired
    private ForgetThePassWordService forgetThePassWordService;

    @Autowired
    private AppUserService appUserService;
    /**
     * 发送忘记密码重置功能的验证码
     * @return
     */
    @RequestMapping("/sendVerificationCode")
    @ResponseBody
    @ApiOperation(value = "发送忘记密码重置功能的验证码", httpMethod = "GET", notes = "发送忘记密码重置功能的验证码")
    public ActResultDto SendVerificationCode(@ApiParam(required =true, name = "手机号", value = "手机号") Optional<String> mobile){
        return forgetThePassWordService.forgetThePassWord(RedisKey.sz_wangji_mobile,mobile);
    }


    /**
     * 验证验证码
     * @param mobile
     * @param code
     * @return
     */
    @RequestMapping("/verificationCode")
    @ResponseBody
    @ApiOperation(value = "验证验证码", httpMethod = "GET", notes = "验证验证码")
    public ActResultDto verificationCode(@ApiParam(required =true, name = "手机号", value = "手机号") Optional<String> mobile ,
                                         @ApiParam(required =true, name = "验证码", value = "验证码") Optional<String> code){
        return forgetThePassWordService.verificationCode(mobile, code, appUserService, RedisKey.sz_wangji_mobile);
    }

    /**
     * 设置新密码
     * @param mobile
     * @return
     */
    @RequestMapping("/setNewPassword")
    @ResponseBody
    @ApiOperation(value = "设置新密码", httpMethod = "GET", notes = "设置新密码")
    public ActResultDto setNewPassword(@ApiParam(required =true, name = "手机号", value = "手机号") Optional<String> mobile ,
                                       @ApiParam(required =true, name = "新密码", value = "新密码") Optional<String> password){
        return forgetThePassWordService.setNewPassword(mobile, password, appUserService);
    }

    /**
     * 个人中心-账号与安全发送验证码
     * @param mobile
     * @return
     */
    @RequestMapping("/personalCenterSendVerificationCode.user")
    @ResponseBody
    @ApiOperation(value = "个人中心-账号与安全发送验证码", httpMethod = "GET", notes = "个人中心-账号与安全发送验证码")
    public ActResultDto personalCenterSendVerificationCode(HttpServletRequest request ,@ApiParam(required =true, name = "手机号", value = "手机号") Optional<String> mobile ,
                                       @ApiParam(required =true, name = "用户ID", value = "用户ID") Optional<Long> id){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return forgetThePassWordService.forgetThePersonalCenter(RedisKey.sz_wangji_mobile, mobile, token.getId());
    }

    /**
     * 个人中心-设置新密码
     * @param mobile
     * @return
     */
    @RequestMapping("/personalCenterSetNewPassword.user")
    @ResponseBody
    @ApiOperation(value = "个人中心设置新密码", httpMethod = "GET", notes = "个人中心设置新密码")
    public ActResultDto personalCenterSetNewPassword(HttpServletRequest request ,@ApiParam(required =true, name = "手机号", value = "手机号") Optional<String> mobile ,
                                       @ApiParam(required =true, name = "新密码", value = "新密码") Optional<String> password,
                                                     @ApiParam(required =true, name = "用户ID", value = "用户ID") Optional<Long> id){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return forgetThePassWordService.setNewPasswordByIDAndBd(mobile, password, token.getId());
    }
}
