package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.DataChargeRecordService;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by admin on 2017/11/2.
 */
@RequestMapping("/flowRecord")
@Controller
public class DataChargeRecordController {
    private static Logger log = LoggerFactory.getLogger(DataChargeRecordController.class);

    @Autowired
    DataChargeRecordService dataChargeRecordService;

    /**
     * 充值流量记录
     * @param request
     * @param pageSize
     * @param offset
     * @return
     */
    @RequestMapping("/getAllRecord.user")
    @ResponseBody
    @ApiOperation(value = "充值流量记录", httpMethod = "GET", notes = "充值流量记录")
    public ActResultDto getAllRecord(HttpServletRequest request,Integer pageSize , Integer offset){
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Map map = dataChargeRecordService.getAllRecordByAppIdPage(pageSize, offset, token.getId());
        String isMore = map.get("isMore").toString();
        if("2".equals(isMore)){
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return resultDto;
        }else{
            map.remove("isMore");
        }
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        resultDto.setData(map);
        resultDto.setExt(isMore);
        return resultDto;
    }
}
