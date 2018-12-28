package com.longlian.live.controller;

import com.huaxin.util.bank.BankCardNoCheckUtil;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.BankCardService;
import com.longlian.model.BankCard;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * Created by admin on 2017/2/25.
 */
@RequestMapping("/bankCard")
@Controller
public class BankCardController {
    private static Logger log = LoggerFactory.getLogger(BankCardController.class);

    @Autowired
    BankCardService bankCardService;

    /**
     * 查询我的银行卡信息
     * @param request
     * @return
     */
    @RequestMapping("/getMyBank.user")
    @ResponseBody
    @ApiOperation(value = "查询我的银行卡信息", httpMethod = "GET", notes = "查询我的银行卡信息")
    public ActResultDto getMyBank(HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return bankCardService.getMyBank(token.getId());
    }

    /**
     * 获取所有的银行
     * @return
     */
    @RequestMapping("/getAllBank")
    @ResponseBody
    @ApiOperation(value = "获取所有的银行", httpMethod = "GET", notes = "获取所有的银行")
    public ActResultDto getAllBank(){
        return bankCardService.getAllBank();
    }

    /**
     * 添加我的银行卡 app
     * @return
     */
    @RequestMapping(value = "/insertAppBankCard.user",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "添加我的银行卡-app", httpMethod = "POST", notes = "添加我的银行卡-app")
    public ActResultDto insertAppBankCard(BankCard bankCard , HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        bankCard.setAppId(token.getId());
        bankCard.setMobile(token.getMobile());
        bankCard.setCreateTime(new Date());
        
        //校验银行账号是否合法
        if(!BankCardNoCheckUtil.checkCardNo(bankCard.getCardNo())) {
    		return new ActResultDto(ReturnMessageType.BANK_CARDNO_INVALID.getCode());
    	}
        //校验银行卡是否存在
        ActResultDto dto = bankCardService.findBankCardByCardNo(bankCard.getCardNo());
        if(dto != null && dto.getCode().equals(ReturnMessageType.CODE_MESSAGE_TRUE.getCode())){
            dto.setCode(ReturnMessageType.BANK_CARD_ALREADY_EXISTS.getCode());
            dto.setMessage(ReturnMessageType.BANK_CARD_ALREADY_EXISTS.getMessage());
            return dto;
        }
        return bankCardService.insertBankCard(bankCard);
    }

    /**
     * 添加我的银行卡
     * @param map
     * @return
     */
    @RequestMapping(value = "/insertBankCard.user",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "添加我的银行卡", httpMethod = "POST", notes = "添加我的银行卡")
    public ActResultDto insertBankCard(@RequestBody Map map , HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        BankCard bankCard = new BankCard();
        bankCard.setAppId(token.getId());
        bankCard.setMobile(token.getMobile());
        bankCard.setCreateTime(new Date());
        bankCard.setBankId(Long.parseLong(String.valueOf(map.get("bankId"))));
        bankCard.setName(String.valueOf(map.get("name")));
        bankCard.setCardNo(String.valueOf(map.get("cardNo")));
        bankCard.setIdCard(String.valueOf(map.get("idCard")));
        bankCard.setBankName(String.valueOf(map.get("bankName")));
        
        //校验银行账号是否合法
        if(!BankCardNoCheckUtil.checkCardNo(bankCard.getCardNo())) {
    		return new ActResultDto(ReturnMessageType.BANK_CARDNO_INVALID.getCode());
    	}
        //校验银行卡是否存在
        ActResultDto dto = bankCardService.findBankCardByCardNo(bankCard.getCardNo());
        if(dto != null && dto.getCode().equals(ReturnMessageType.CODE_MESSAGE_TRUE.getCode())){
            dto.setCode(ReturnMessageType.BANK_CARD_ALREADY_EXISTS.getCode());
            dto.setMessage(ReturnMessageType.BANK_CARD_ALREADY_EXISTS.getMessage());
            return dto;
        }
        return bankCardService.insertBankCard(bankCard);
    }

    /**
     * 银行卡详情
     * @param id
     * @return
     */
    @RequestMapping("/findBankCardInfo.user")
    @ResponseBody
    @ApiOperation(value = "银行卡详情", httpMethod = "GET", notes = "银行卡详情")
    public ActResultDto findBankCardInfo(@ApiParam(required = true,name = "银行卡ID",value = "银行卡ID")Long id){
        return bankCardService.findBankCardInfo(id);
    }

    /**
     * 解绑银行卡
     * @param id
     * @return
     */
    @RequestMapping("/delBankCardById.user")
    @ResponseBody
    @ApiOperation(value = "解绑银行卡", httpMethod = "GET", notes = "解绑银行卡")
    public ActResultDto delBankCardById(@ApiParam(required = true,name = "银行卡ID",value = "银行卡ID")Long id , HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return bankCardService.delBankCardById(id,token.getId());
    }
}
