package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.service.PushMsgService;
import com.longlian.dto.ActResultDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.model.PushMsg;
import com.longlian.token.AppUserIdentity;
import com.longlian.token.ConsoleUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    /**
     * 获取H5推送记录列表
     */
    @RequestMapping(value = "/getH5PushMsgListPage.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "H5推送记录列表", httpMethod = "POST", notes = "H5推送记录列表")
    public DataGridPage getH5PushMsgListPage(DataGridPage dataGridPage, @RequestParam Map map) {
        try {
            dataGridPage = pushMsgService.getH5PushMsgListPage(dataGridPage, map);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("获取H5推送记录列表异常");
        }
        return dataGridPage;
    }

    /**
     * 极光推送H5信息
     * @param request
     * @param pushMsg
     * @return
     */
    @RequestMapping(value = "/insertH5PushMsg.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "H5页面极光推送", httpMethod = "POST", notes = "H5页面极光推送")
    public ActResult insertH5PushMsg(HttpServletRequest request,PushMsg pushMsg){
        ActResult actResult = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            if(null != token){
                pushMsg.setId(token.getId());
            }
            pushMsgService.insertH5PushMsg(pushMsg);
            actResult.setSuccess(true);
            actResult.setMsg("H5页面极光推送成功");
        } catch (Exception e) {
            actResult.setSuccess(false);
            actResult.setMsg("H5页面极光推送异常");
            log.error("H5页面极光推送异常", e);
        }
        return actResult;
    }

    /**
     * 极光消息推送跳转页面
     * @return
     */
    @RequestMapping("/toH5MsgPushPage")
    public ModelAndView toH5MsgPushPage(){
        ModelAndView view = new ModelAndView("/func/push/h5Push");
        return view;
    }
    /**
     * 极光消息推送列表
     * @return
     */
    @RequestMapping("/toH5MsgPushList")
    public ModelAndView toH5MsgPushList(){
        ModelAndView view = new ModelAndView("/func/push/h5PushList");
        return view;
    }

}