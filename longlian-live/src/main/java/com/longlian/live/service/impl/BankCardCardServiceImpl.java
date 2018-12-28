package com.longlian.live.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huaxin.util.JsonUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.BankCardDto;
import com.longlian.live.dao.BankCardMapper;
import com.longlian.live.dao.BankMapper;
import com.longlian.live.service.BankCardService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.model.Bank;
import com.longlian.model.BankCard;
import com.longlian.type.LogTableType;
import com.longlian.type.LogType;
import com.longlian.type.ReturnMessageType;

/**
 * Created by admin on 2017/2/25.
 */
@Service("bankCardService")
public class BankCardCardServiceImpl implements BankCardService {
    private static Logger log = LoggerFactory.getLogger(BankCardCardServiceImpl.class);

    @Autowired
    BankCardMapper bankCardMapper;
    @Autowired
    BankMapper bankMapper;

    /**
     * 获取我的银行卡信息
     * @param appId
     * @return
     */
    @Override
    public ActResultDto getMyBank(long appId) {
        ActResultDto resultDto = new ActResultDto();
        List<BankCardDto> bankList = bankCardMapper.findCardsByAppId(appId);
        if(bankList!=null && bankList.size()>0){
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setData(bankList);
        }else{
            resultDto.setCode(ReturnMessageType.NOT_BANK_CARD_APP_USER.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_BANK_CARD_APP_USER.getMessage());
        }
        return resultDto;
    }

    /**
     * 绑定银行卡
     * @param bankCard
     * @return
     */
    @Override
    public ActResultDto insertBankCard(BankCard bankCard) {
        ActResultDto resultDto = new ActResultDto();
        try {
            bankCardMapper.insertBankCard(bankCard);
            Map map = new HashMap<>();
            map.put("卡号",bankCard.getCardNo());
            map.put("银行",bankCard.getBankName());
            map.put("添加时间:",new Date());
            SystemLogUtil.saveSystemLog(0, LogType.add_bank.getType()+"",bankCard.getAppId(),bankCard.getName(),JsonUtil.toJson(map),"添加银行卡", LogTableType.def.getVal(),bankCard.getId());
            log.info("添加银行卡信息:{}" + JsonUtil.toJson(bankCard));
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        }catch (Exception e){
            resultDto.setCode(ReturnMessageType.INSERT_BANK_CARD_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.INSERT_BANK_CARD_ERROR.getMessage());
        }
        return resultDto;
    }

    /**
     * 获取银行卡信息
     * @param id
     * @return
     */
    @Override
    public ActResultDto findBankCardInfo(Long id) {
        ActResultDto resultDto = new ActResultDto();
        if(id!=null && id >0){
            BankCardDto BankCardDto = bankCardMapper.getBankCardById(id);
            if(BankCardDto==null){
                resultDto.setCode(ReturnMessageType.GET_BANK_CARD_INFO_ERROR.getCode());
                resultDto.setMessage(ReturnMessageType.GET_BANK_CARD_INFO_ERROR.getMessage());
                return  resultDto;
            }else{
                resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                resultDto.setData(BankCardDto);
                return  resultDto;
            }
        }else{
            resultDto.setCode(ReturnMessageType.NOT_BANK_CARD_APP_USER.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_BANK_CARD_APP_USER.getMessage());
            return resultDto;
        }
    }

    /**
     * 解绑银行卡信息
     * @param id
     * @return
     */
    @Override
    public ActResultDto delBankCardById(Long id , Long appId) {
        ActResultDto resultDto = new ActResultDto();
        try {
            BankCardDto bankCardDto = bankCardMapper.getBankCardById(id);
            if(bankCardDto != null){
                bankCardMapper.deleteByPrimaryKey(id);
                Map map = new HashMap<>();
                map.put("卡号",bankCardDto.getCardNo());
                map.put("银行",bankCardDto.getBankName());
                map.put("解除时间:", new Date());
                SystemLogUtil.saveSystemLog(0, LogType.del_bank.getType() + "", appId , bankCardDto.getName(), JsonUtil.toJson(map), "解绑银行卡", LogTableType.def.getVal(), id);
            }
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        }catch (Exception e){
            resultDto.setMessage(ReturnMessageType.DEL_BANK_CARD_ERROR.getMessage());
            resultDto.setCode(ReturnMessageType.DEL_BANK_CARD_ERROR.getCode());
        }
        return resultDto;
    }

    /**
     * 获取所有的银行
     * @return
     */
    @Override
    public ActResultDto getAllBank() {
        ActResultDto resultDto = new ActResultDto();
        List<Bank> list = bankMapper.getAllBank();
        if(list!=null && list.size()>0){
            resultDto.setData(list);
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        }else{
            resultDto.setCode(ReturnMessageType.GET_BANK_CARD_INFO_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.GET_BANK_CARD_INFO_ERROR.getMessage());
        }
        return resultDto;
    }

    /**
     * 查询银行卡信息
     * @param cardNo
     * @return
     */
    @Override
    public ActResultDto findBankCardByCardNo(String cardNo) {
        List<BankCardDto> bankCards = bankCardMapper.findBankCardByCardNo(cardNo);
        if(CollectionUtils.isNotEmpty(bankCards)){
        	ActResultDto dto = new ActResultDto(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            dto.setData(bankCards.get(0));
            return dto;
        }
        return null;
    }
}
