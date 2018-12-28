package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AccountService;
import com.longlian.model.Account;
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
import java.util.List;

/**
 * Created by admin on 2017/2/27.
 */
@RequestMapping("/account")
@Controller
public class AccountController {
    private static Logger log = LoggerFactory.getLogger(AccountController.class);

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
    public ActResultDto getAccount(HttpServletRequest request) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Account account = accountService.getAccountByUserId(token.getId());
        result.setData(account.getBalance());
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
    public ModelAndView findUserAccount(HttpServletRequest request
            , @ApiParam(required =true, name = "类型", value = "类型") String type
            , @ApiParam(required =true, name = "课程ID", value = "课程ID")  String courseId) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Account account = accountService.getAccountByUserId(token.getId());
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
    @ApiOperation(value = "设置交易密码", httpMethod = "POST", notes = "设置交易密码")
    public ActResultDto setPwd( @ApiParam(required =true, name = "密码", value = "密码") String pwd , HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return accountService.resetTradePassword(token.getId(),pwd);
    }

    /**
     * 发送验证码
     *
     * @return
     */
    @RequestMapping(value = "/sendCode.user")
    @ApiOperation(value = "发送验证码", httpMethod = "GET", notes = "发送验证码")
    public ModelAndView sendCode(HttpServletRequest request
            , @ApiParam(required =true, name = "类型", value = "类型") String type
            ,@ApiParam(required =true, name = "课程ID", value = "课程ID") String courseId) {
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
        return accountService.sendCheckCode(token.getMobile());
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
    public ActResultDto checkTradePwd(HttpServletRequest request, @ApiParam(required =true, name = "原密码", value = "原密码")  @RequestParam("password") String tradePwd) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return accountService.checkTradePassword(token.getId(), tradePwd);
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
    public ActResultDto resetTradePwd(HttpServletRequest request,@ApiParam(required =true, name = "新交易密码", value = "新交易密码") @RequestParam("password") String newPassword) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return accountService.resetTradePassword(token.getId(), newPassword);
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
    public ActResultDto checkCode(@ApiParam(required =true, name = "验证码", value = "验证码") @RequestParam("code") String code, HttpServletRequest request) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return accountService.checkCode(code, token.getMobile());
    }

    /**
     * 判断课程支付,钱包余额是否足够
     * @param courseMoney
     * @param request
     * @return
     */
    @RequestMapping("/isPay.user")
    @ResponseBody
    @ApiOperation(value = "判断课程支付,钱包余额是否足够", httpMethod = "GET", notes = "判断课程支付,钱包余额是否足够")
    public ActResultDto isPay(@ApiParam(required =true, name = "课程价格", value = "课程价格") String courseMoney,HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return accountService.findBalanceIsPay(courseMoney, token.getId());
    }

    /**
     * 枣币兑换学币
     * @param request
     * @return
     */
    @RequestMapping(value = "/transforZb2Xb.user")
    @ResponseBody
    @ApiOperation(value = "枣币兑换为学币", httpMethod = "GET", notes = "枣币兑换为学币")
    public ActResultDto transforZb2Xb(@ApiParam(required = true, name = "兑换枣币金额", value = "兑换枣币金额") String amount,
                                      HttpServletRequest request){
        ActResultDto dto = new ActResultDto();
        if(amount == null || amount.length() < 1){
            dto.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
            dto.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            return dto;
        }
        if(!(amount instanceof String)){
            dto.setCode(ReturnMessageType.CODE_PARAM_TYPE_ERROR.getCode());
            dto.setMessage(ReturnMessageType.CODE_PARAM_TYPE_ERROR.getMessage());
            return dto;
        }
        AppUserIdentity identity = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(identity != null){
            String isChange = accountService.getIsZbTransXb();
            //如果开放了 枣币兑换学币 ，则 可以兑换
            if(isChange != null && "1".equals(isChange)){
                dto = accountService.transforZb2Xb(identity.getId(),amount);
            }else{
                dto.setCode(ReturnMessageType.NO_ZB_TRANS_XB.getCode());
                dto.setMessage(ReturnMessageType.NO_ZB_TRANS_XB.getMessage());
            }
        }
        return dto;
    }

    @RequestMapping(value = "getAccountTransRecord.user")
    @ResponseBody
    @ApiOperation(value = "获取枣币转换学币记录", httpMethod = "GET", notes = "获取枣币转换学币记录")
    public ActResultDto getAccountTransRecord(@ApiParam(required = true, name = "当前页码", value = "1") Integer pageNum,
                                              @ApiParam(required = true, name = "每页条数", value = "10") Integer pageSize,
                                              HttpServletRequest request){
        ActResultDto ac = new ActResultDto();
        //参数判断
        if(pageNum == null || pageSize == null){
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return ac;
        }
        if(!(pageNum instanceof Integer) || !(pageSize instanceof Integer)){
            ac.setCode(ReturnMessageType.CODE_PARAM_TYPE_ERROR.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_TYPE_ERROR.getMessage());
            return ac;
        }
        if(pageNum <= 0 || pageSize <= 0){
            ac.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            return ac;
        }
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(token != null){
            List list = accountService.getAccountTransRecord(token.getId(),pageNum,pageSize);
            if(list != null && list.size() > 0){
                ac.setData(list);
                ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                return ac;
            }else{
                ac.setCode(ReturnMessageType.NO_DATA.getCode());
                ac.setMessage(ReturnMessageType.NO_DATA.getMessage());
                return ac;
            }
        }
        ac.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
        ac.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
        return ac;
    }

}
