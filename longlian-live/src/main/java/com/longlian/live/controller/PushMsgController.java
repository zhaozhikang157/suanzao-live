package com.longlian.live.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.LiveRoomDto;
import com.longlian.live.service.PushMsgService;
import com.longlian.model.PushMsg;
import com.longlian.token.ConsoleUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/17.
 */
@Controller
@RequestMapping(value = "/pushMsg")
public class PushMsgController {
    private static Logger log = LoggerFactory.getLogger(PushMsgController.class);

    @Autowired
    PushMsgService pushMsgService;

    /**
     *系统消息列表
     *
     */
    @RequestMapping(value = "/getPushMsgList.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "系统消息列表", httpMethod = "GET", notes = "系统消息列表")
    public ActResultDto getPushMsgList(@ApiParam(required =true, name = "分页页数", value = "分页页数")Integer pageNum,@ApiParam(required =true, name = "每页条数", value = "每页条数")Integer pageSize) {
        ActResultDto ac =  new ActResultDto();
        List<Map> list = pushMsgService.getPushMsgList(pageNum,pageSize);
        if (list != null && list.size() > 0) {
            ac.setData(list);
            ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            ac.setMessage(ReturnMessageType.NO_DATA.getMessage());
            ac.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return ac;
    }

    /**
     *系统消息数据回显
     *
     */
    @RequestMapping(value = "/getPushMsgById.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "系统消息数据回显", httpMethod = "GET", notes = "系统消息数据回显")
    public ActResultDto getPushMsgById(@ApiParam(required =true, name = "消息ID", value = "消息ID")Long id) {
        ActResultDto ac =  new ActResultDto();
        if (id==null){
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        }else{
            ac.setData(pushMsgService.getPushMsgById(id));
        }
        return ac;
    }

}