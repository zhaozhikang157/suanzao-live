package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.WithdrawalsService;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.token.AppUserIdentity;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2016/8/19.
 * 提现
 */
@RequestMapping("/withdrawals")
@Controller
public class WithdrawalsController {

    @Autowired
    WithdrawalsService withdrawalsService;
    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;

    /**
     * 钱包信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/showAccountInfo.user",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "钱包信息", httpMethod = "POST", notes = "钱包信息")
    public ActResultDto showAccountBalance(HttpServletRequest request,
                                           @ApiParam(required =true, name = "ID", value = "ID")String cardId){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return withdrawalsService.findAccountBalance(token.getId(),cardId);
    }

    /**
     * 提现操作
     * @param request
     * @param amount
     * @param cardId
     * @return
     */
    @RequestMapping(value = "/option.user" , method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "提现操作", httpMethod = "POST", notes = "提现操作")
    public ActResultDto option(HttpServletRequest request,
                               @ApiParam(required =true, name = "金额", value = "金额") BigDecimal amount ,
                               @ApiParam(required =true, name = "ID", value = "ID")  Long cardId,
                               @ApiParam(required =true, name = "密码", value = "密码") String tradePassword,
                               @ApiParam(required =true, name = "类型", value = "类型") String type){
        AppUserIdentity token = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return withdrawalsService.bankOutOption(token, amount, cardId,tradePassword,type);
    }

    /**
     *  提现说明
     * @return
     */
    @RequestMapping(value = "/getBankOutRemark.user")
    @ResponseBody
    @ApiOperation(value = "提现操作", httpMethod = "GET", notes = "提现操作")
    public ActResultDto getBankOutRemark(HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = new ActResultDto();
        Map map = new HashMap<>();
        map.put("bankOutRemark",systemParaRedisUtil.getBankOutRemark(token.getId()));
        map.put("bankOutRemarkProxy",systemParaRedisUtil.getBankOutRemarkProxy());
        resultDto.setData(map);
        return resultDto;
    }
}
