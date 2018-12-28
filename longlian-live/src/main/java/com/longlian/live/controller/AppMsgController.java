package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AppMsgService;
import com.longlian.token.AppUserIdentity;
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
 * Created by pangchao on 2017/2/22.
 */
@Controller
@RequestMapping(value = "/appMsg")
public class AppMsgController {
    private static Logger log = LoggerFactory.getLogger(AppMsgController.class);

    @Autowired
    AppMsgService appMsgService;

    /**
     * 查看系统消息列表
     */
    @RequestMapping(value = "/getAppMsgList.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查看系统消息列表", httpMethod = "GET", notes = "查看系统消息列表")
    public ActResultDto getAppMsgList(HttpServletRequest request, Integer pageNum, Integer pageSize) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            List<Map> list = appMsgService.getAppMsgList(token.getId(), pageNum, pageSize);
            if (list.size() == 0) {
                result.setCode(ReturnMessageType.NO_DATA.getCode());
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            } else {
                result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            }
            result.setData(list);
        }
        return result;
    }

    /**
     * 查看系统消息 大类
     */
    @RequestMapping(value = "/getNewAppMsgTypeInfo.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查看系统消息分类列表", httpMethod = "GET", notes = "查看系统消息分类列表")
    public ActResultDto getNewAppMsgTypeInfo(HttpServletRequest request) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            List<Map> list = appMsgService.getNewAppMsgTypeInfo(token.getId(),request.getParameter("v"));
            if (list.size() == 0) {
                result.setCode(ReturnMessageType.NO_DATA.getCode());
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            } else {
                result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            }
            result.setData(list);
        }
        return result;
    }

    /**
     * 查看系统消息 大类下的子消息
     */
    @RequestMapping(value = "/getNewAppMsgTypeList.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查看系统消息列表", httpMethod = "GET", notes = "查看系统消息列表")
    public ActResultDto getNewAppMsgTypeList(HttpServletRequest request, String type,Integer pageNum, Integer pageSize) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            List<Map> list = appMsgService.getNewAppMsgTypeList(type, token.getId(), pageNum, pageSize);
            if (list.size() == 0) {
                result.setCode(ReturnMessageType.NO_DATA.getCode());
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            } else {
                result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            }
            result.setData(list);
        }
        return result;
    }

    /**
     * 根据大类修改系统消息为已读
     */
    @RequestMapping(value = "/updateStatusByMsgType.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据大类修改系统消息为已读", httpMethod = "GET", notes = "根据大类修改系统消息为已读")
    public ActResultDto updateStatusByMsgType(HttpServletRequest request, String type) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            appMsgService.updateStatusByMsgType(type, token.getId());
        }
        return result;
    }

    /**
     * 判断是否有消息提醒
     */
    @RequestMapping(value = "/checkIsHaveMsg.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "判断是否有消息提醒", httpMethod = "GET", notes = "判断是否有消息提醒")
    public ActResultDto checkIsHaveMsg(HttpServletRequest request, String type) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> list = appMsgService.getNewAppMsgTypeList(type, token.getId(), 0, 5);
        if(list==null || list.size()==0){
            result.setCode(ReturnMessageType.MSG_IS_EMPTY.getCode());
            result.setMessage(ReturnMessageType.MSG_IS_EMPTY.getMessage());
            return result;
        }
        return result;
    }

    /**
     * APP删除系统消息 批量
     */
    @RequestMapping(value = "/deleteAppMsgByIds.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "APP删除系统消息", httpMethod = "POST", notes = "APP删除系统消息")
    public ActResultDto deleteAppMsgByIds(HttpServletRequest request, String ids) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (ids == null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            try {
                String []idsArr=ids.split(",");
                Long []strArrNum=new Long[idsArr.length];
                for(int i=0;i<idsArr.length;i++){
                    strArrNum[i] = Long.valueOf(idsArr[i]);
                }
                appMsgService.deleteAppMsgByIds(strArrNum);
            } catch (Exception e) {
                e.printStackTrace();
                result.setMessage("删除系统消息失败!!");
                //throw e;
            }
        }
        return result;
    }

    /**
     * 微信删除系统消息 批量
     */
    @RequestMapping(value = "/deleteAppMsgByIdsAndType.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "微信删除系统消息", httpMethod = "POST", notes = "微信删除系统消息")
    public ActResultDto deleteAppMsgByIdsAndType(HttpServletRequest request, String ids,String type) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (ids == null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            try {
                String []idsArr=ids.split(",");
                Long []strArrNum=new Long[idsArr.length];
                for(int i=0;i<idsArr.length;i++){
                    strArrNum[i] = Long.valueOf(idsArr[i]);
                }
                appMsgService.deleteAppMsgByIds(strArrNum);
                List<Map> list = appMsgService.getNewAppMsgTypeList(type, token.getId(), 0, 5);
                if(list==null || list.size()==0){
                    result.setCode(ReturnMessageType.MSG_IS_EMPTY.getCode());
                    result.setMessage(ReturnMessageType.MSG_IS_EMPTY.getMessage());
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setMessage("删除系统消息失败!!");
                //throw e;
            }
        }
        return result;
    }

    /**
     * 读取系统消息
     */
    @RequestMapping(value = "/updateAppMsg.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "读取系统消息", httpMethod = "GET", notes = "读取系统消息")
    public ActResultDto updateAppMsg(@ApiParam(required = true, name = "消息ID", value = "消息ID") Long id) throws Exception {
        ActResultDto result = new ActResultDto();
        if (id == null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            try {
                Map map = appMsgService.updateAppMsg(id);
                result.setData(map);
            } catch (Exception e) {
                e.printStackTrace();
                result.setMessage("读取系统消息失败!!");
                //throw e;
            }
        }
        return result;
    }

    /**
     * 删除系统消息
     */
    @RequestMapping(value = "/deleteAppMsg.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除系统消息", httpMethod = "GET", notes = "删除系统消息")
    public ActResultDto deleteAppMsg(HttpServletRequest request,
                                     @ApiParam(required = true, name = "消息ID", value = "消息ID") String id) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (id == null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            try {
                appMsgService.deleteAppMsg(id);
            } catch (Exception e) {
                e.printStackTrace();
                result.setMessage("删除系统消息失败!!");
                //throw e;
            }
        }
        return result;
    }


    /**
     * 清空系统消息
     */
    @RequestMapping(value = "/emptyAppMsg.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "清空系统消息", httpMethod = "GET", notes = "清空系统消息")
    public ActResultDto emptyAppMsg(HttpServletRequest request) throws Exception {
        log.info("系统消息全部清空开始");
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(token==null){
            result.setCode(ReturnMessageType.CODE_UPDATE_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_UPDATE_RETURN.getMessage());
            log.info("系统消息已全部清空失败，token为空");
            return result;
        }
        log.info("清空系统消息开始，tokenId为："+token.getId());
        try {
            List<Map> list = appMsgService.getNewAppMsgTypeList("", token.getId(), 0, 5);
            if(list==null || list.size()==0){
                result.setCode(ReturnMessageType.MSG_IS_EMPTY.getCode());
                result.setMessage(ReturnMessageType.MSG_IS_EMPTY.getMessage());
                log.info("系统消息已全部清空");
                return result;
            }
            appMsgService.deleteAllAppMsg(token.getId());
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("清空系统消息失败!!");
            //throw e;
        }
        log.info("系统消息全部清空结束");
        return result;
    }

    /**
     * 读取所有的系统消息
     */
    @RequestMapping(value = "/readAllMessage.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "读取所有系统消息", httpMethod = "GET", notes = "读取所有系统消息")
    public ActResultDto readAllMessage(HttpServletRequest request) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        try {
            appMsgService.readAllMessage(token.getId());
        } catch (Exception e) {
            log.error("读取所有的系统消息失败", e);
            result.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
            result.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
        }
        return result;
    }

    /**
     * 新版系统消息列表
     */
    @RequestMapping(value = "/getAppMsgListV2.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "新版系统消息列表", httpMethod = "POST", notes = "新版系统消息列表")
    public ActResultDto getAppMsgListV2(HttpServletRequest request, Integer pageNum, Integer pageSize) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            List<Map> list = appMsgService.getAppMsgList(token.getId(), pageNum, pageSize);
            if (list.size() == 0) {
                result.setCode(ReturnMessageType.NO_DATA.getCode());
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            } else {
                result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            }
            result.setData(list);
        }
        return result;
    }
}