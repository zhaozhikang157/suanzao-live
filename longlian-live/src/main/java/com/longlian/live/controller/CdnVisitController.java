package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CdnVisitService;
import com.longlian.live.service.LiveRoomService;
import com.longlian.model.LiveRoom;
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
 * Created by admin on 2017/11/3.
 */
@RequestMapping("/cdnVisit")
@Controller
public class CdnVisitController {
    private static Logger log = LoggerFactory.getLogger(CdnVisitController.class);

    @Autowired
    CdnVisitService cdnVisitService;
    @Autowired
    LiveRoomService liveRoomService;

    @RequestMapping("/getAllCourseFlow.user")
    @ResponseBody
    @ApiOperation(value = "课程流量消耗", httpMethod = "GET", notes = "课程流量消耗")
    public ActResultDto getAllRecord(HttpServletRequest request,Integer pageSize , Integer offset){
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        LiveRoom liveRoom = liveRoomService.findByAppId(token.getId());
        if(liveRoom==null){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        Map map = cdnVisitService.getAllCourseFlowPage(pageSize, offset, liveRoom.getId());
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
