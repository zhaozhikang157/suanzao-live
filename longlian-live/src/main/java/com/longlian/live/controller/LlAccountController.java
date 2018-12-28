package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.LlAccountService;
import com.longlian.model.Account;
import com.longlian.model.LlAccount;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/2/27.
 */
@RequestMapping("/llaccount")
@Controller
public class LlAccountController {
    private static Logger log = LoggerFactory.getLogger(LlAccountController.class);

    @Autowired
    LlAccountService llAccountService;

    @Autowired
    AccountService accountService;


    /**
     * 获取用户账户余额
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getAccountBalance.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取用户账户余额", httpMethod = "GET", notes = "获取用户账户余额")
    public ActResultDto getAccount(@ApiParam(required = true, name = "app版本", value = "app版本") String v,
                                   HttpServletRequest request) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        LlAccount account = llAccountService.getAccountByUserId(token.getId()); //学币
        Account zbAccount = accountService.getAccountByUserId(token.getId()); //枣币
        if(account == null || zbAccount == null){
            result.setCode(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getCode());
            result.setMessage(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getMessage());
            return result;
        }
        if(v != null && v.length() > 0 && Integer.valueOf(v.replace(".","")) <= 163){ //兼容1.6.3之前的版本
            result.setData(account.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP));
        }else{
            Map acMap = new HashMap() ;
            if(account.getBalance().compareTo(new BigDecimal(0))>0){
                acMap.put("xb",account.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP));
            }else{
                acMap.put("xb",new BigDecimal(0));
            }
            if(zbAccount.getBalance().compareTo(new BigDecimal(0))>0){
                acMap.put("zb",zbAccount.getBalance().setScale(2,BigDecimal.ROUND_HALF_UP));
            }else{
                acMap.put("zb",new BigDecimal(0));
            }
            result.setData(acMap);
        }
        if(StringUtils.isEmpty(account.getTradePwd())){
            result.setExt("0");
        }else{
            result.setExt("1");
        }
        return result;
    }

    /**
     * 判断是设置交易密码还是重置
     * @param request
     * @param type
     * @param courseId
     * @return
     */
    @RequestMapping("/findUserAccount.user")
    @ApiOperation(value = "判断是设置交易密码还是重置", httpMethod = "GET", notes = "判断是设置交易密码还是重置")
    public ModelAndView findUserAccount(HttpServletRequest request,String type,String courseId) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        LlAccount account = llAccountService.getAccountByUserId(token.getId());
        ModelAndView view = new ModelAndView("/func/weixin/myBank/reset_trade_pwd");
        if (StringUtils.isEmpty(account.getTradePwd())) {
            view = new ModelAndView("/func/weixin/myBank/dealPwdPage");
        }
        view.addObject("type",type);
        view.addObject("courseId", courseId);
        return view;
    }

    /**
     * 设置交易密码
     * @param pwd
     * @param request
     * @return
     */
    @RequestMapping("/setPwd.user")
    @ResponseBody
    @ApiOperation(value = "设置交易密码", httpMethod = "GET", notes = "设置交易密码")
    public ActResultDto setPwd( @ApiParam(required = true,name = "交易密码",value = "交易密码")String  pwd,
                               HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return llAccountService.resetTradePassword(token.getId(),pwd);
    }

    /**
     * 发送验证码
     *
     * @return
     */
    @RequestMapping(value = "/sendCode.user")
    @ApiOperation(value = "发送验证码", httpMethod = "GET", notes = "发送验证码")
    public ModelAndView sendCode(HttpServletRequest request,String type,String courseId) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ModelAndView view = new ModelAndView("/func/weixin/myBank/send_code");
        view.addObject("mobile",token.getMobile());
        view.addObject("type",type);
        view.addObject("courseId",courseId);
        return view;
    }

    /**
     * 发送验证码
     *
     * @param request
     * @return
     */
    @RequestMapping("/sendCheckCode.user")
    @ResponseBody
    @ApiOperation(value = "发送验证码", httpMethod = "GET", notes = "发送验证码")
    public ActResultDto sendCheckCode(HttpServletRequest request) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return llAccountService.sendCheckCode(token.getMobile());
    }

    /**
     * 校验原密码
     *
     * @param request
     * @param tradePwd
     * @return
     */
    @RequestMapping(value = "/checkTradePwd.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "校验原密码", httpMethod = "POST", notes = "校验原密码")
    public ActResultDto checkTradePwd(HttpServletRequest request, @RequestParam("password") String tradePwd) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return llAccountService.checkTradePassword(token.getId(), tradePwd);
    }

    /**
     * 设置新交易密码
     *
     * @param request
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "/resetTradePwd.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "设置新交易密码", httpMethod = "POST", notes = "设置新交易密码")
    public ActResultDto resetTradePwd(HttpServletRequest request, @RequestParam("password") String newPassword) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return llAccountService.resetTradePassword(token.getId(), newPassword);
    }

    /**
     * 校验验证码
     *
     * @param code
     * @param request
     * @return
     */
    @RequestMapping(value = "/checkCode.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "校验验证码", httpMethod = "POST", notes = "校验验证码")
    public ActResultDto checkCode(@RequestParam("code") String code, HttpServletRequest request) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return llAccountService.checkCode(code, token.getMobile());
    }

    /**
     * 判断课程支付,钱包余额是否足够
     * @param courseMoney
     * @param request
     * @return
     */
    @RequestMapping("/isPay.user")
    @ResponseBody
    @ApiOperation(value = "判断课程支付,钱包余额是否足够", httpMethod = "POST", notes = "判断课程支付,钱包余额是否足够")
    public ActResultDto isPay(String courseMoney,HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return llAccountService.findBalanceIsPay(courseMoney, token.getId());
    }
}
