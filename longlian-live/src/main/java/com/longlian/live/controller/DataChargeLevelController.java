package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.DataChargeLevelService;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;


/**
 * Created by admin on 2017/10/31.
 */
@Controller
@RequestMapping(value = "/dataChargeLevel")
public class DataChargeLevelController {

    private static Logger log = LoggerFactory.getLogger(DataChargeLevelController.class);

    @Autowired
    DataChargeLevelService dataChargeLevelService;
    
    /**
     * 流量级别列表
     **/
    @RequestMapping(value = "/getDataChargeLevelList.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "流量级别列表", httpMethod = "GET", notes = "流量级别列表")
    public ActResultDto getDataChargeLevelList(HttpServletRequest request) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto result = new ActResultDto();
        try {
            return dataChargeLevelService.getDataChargeLevelList(token.getId());
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
            result.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
            return result;
        }
    }
}
