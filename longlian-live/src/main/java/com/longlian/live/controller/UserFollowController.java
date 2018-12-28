package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.itextpdf.text.PageSize;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.UserFollowService;
import com.longlian.live.util.Main;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/12.
 */
@Controller
@RequestMapping(value = "/userFollow")
public class UserFollowController {
    private static Logger log = LoggerFactory.getLogger(UserFollowController.class);

    @Autowired
    UserFollowService userFollowService;

    /**
     * 直播间全部关注人
     * @param roomId 直播间ID
     * @return
     */
    @RequestMapping(value = "/getCountUserFollow", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "直播间全部关注人", httpMethod = "GET", notes = "直播间全部关注人")
    public ActResultDto getCountUserFollow(
            @ApiParam(required =true, name = "直播间ID", value = "直播间ID")  Long roomId,
            @ApiParam(required =true, name = "偏移量", value = "偏移量")  Integer offSet,
            @ApiParam(required =true, name = "创建时间", value = "创建时间")   Date createTime) {
        ActResultDto ac =  new ActResultDto();
        if(offSet == null || roomId == null){
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return ac;
        }
        if(!(offSet instanceof Integer) || !(roomId instanceof Long)){
            ac.setCode(ReturnMessageType.CODE_PARAM_TYPE_ERROR.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_TYPE_ERROR.getMessage());
            return ac;
        }
        if(offSet < 0 || roomId < 0){
            ac.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            return ac;
        }
        if (roomId==null){
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        }else{
            ac = userFollowService.getCountUserFollow(roomId,offSet,createTime);
        }
        return ac;
    }
    /**
     * 直播间全部关注人
     * @param roomId 直播间ID
     * @return
     */
    @RequestMapping(value = "/getCountUserFollow.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "直播间全部关注人", httpMethod = "GET", notes = "直播间全部关注人")
    public ActResultDto getCountUserFollowUser(
            @ApiParam(required =true, name = "直播间ID", value = "直播间ID")  Long roomId,
            @ApiParam(required =true, name = "偏移量", value = "偏移量")  Integer offSet,
            @ApiParam(required =true, name = "创建时间", value = "创建时间")   Date createTime) {
        return  getCountUserFollow(  roomId,  offSet,  createTime);
    }
   /**
     * 直播间关注人数
     */
    @RequestMapping(value = "/getCountByRoomId", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "直播间关注人数", httpMethod = "GET", notes = "直播间关注人数")
    public ActResultDto getCountByRoomId(
            @ApiParam(required =true, name = "直播间ID", value = "直播间ID") Long id) {
        ActResultDto ac =  new ActResultDto();
        if (id==null){
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        }else{
            ac.setData(userFollowService.getCountByRoomId(id));
        }
        return ac;
    }

    /**
     * 我关注的直播间
     */
    @RequestMapping(value = "/followLiveRoom.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "我关注的直播间", httpMethod = "GET", notes = "我关注的直播间")
    public ActResultDto followLiveRoom(HttpServletRequest request,
                                       @ApiParam(required =true, name = "页数", value = "页数") Integer pageNum,
                                       @ApiParam(required =true, name = "每页数量", value = "每页数量")  Integer pageSize) {
        ActResultDto ac =  new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token==null){
            ac.setCode(ReturnMessageType.NO_LOGIN.getCode());
            ac.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        }else{
            List<Map> list = userFollowService.followLiveRoom(token.getId(), pageNum, pageSize);
            if(list!=null && list.size()>0){
                ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                ac.setData(list);
            }else {
                ac.setCode(ReturnMessageType.NO_DATA.getCode());
                ac.setMessage(ReturnMessageType.NO_DATA.getMessage());
            }
        }
        return ac;
    }

    /**
     * 我关注的直播间 new
     */
    @RequestMapping(value = "/followLiveRoomNew.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "我关注得直播间", httpMethod = "GET", notes = "我关注得直播间")
    public ActResultDto followLiveRoomNew(HttpServletRequest request,
                                          @ApiParam(required = true,name = "页数", value = "页数") Integer pageNum,
                                          @ApiParam(required = true,name = "每页数量", value = "每页数量") Integer pageSize){
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
        if(token == null){
            ac.setCode(ReturnMessageType.NO_LOGIN.getCode());
            ac.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        }else{
            List<Map> list = userFollowService.followLiveRoomNew(token.getId(), pageNum, pageSize) ;
            if(list != null && list.size() > 0){
                ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                ac.setData(list);
            }else{
                ac.setCode(ReturnMessageType.NO_DATA.getCode());
                ac.setMessage(ReturnMessageType.NO_DATA.getMessage());
            }
        }
        return ac;
    }

    /**
     * follow操作
     * @return
     */
    @RequestMapping(value = "/follow.user")
    @ResponseBody
    @ApiOperation(value = "follow操作", httpMethod = "GET", notes = "follow操作")
    public ActResultDto follow(HttpServletRequest request ,
                               @ApiParam(required =true, name = "ID", value = "Id") Long liveRoomId) {
        AppUserIdentity token = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();
        //判断参数
        if (liveRoomId == null || liveRoomId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        return userFollowService.follow(token.getId(),liveRoomId);
    }

    /**
     * 取消follow
     * @return
     */
    @RequestMapping(value = "/cancelFollow.user")
    @ResponseBody
    @ApiOperation(value = "取消follow", httpMethod = "GET", notes = "取消follow")
    public ActResultDto cancelFollow(HttpServletRequest request , @ApiParam(required =true, name = "ID", value = "Id") Long liveRoomId) {
        AppUserIdentity token = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();
        //判断参数
        if (liveRoomId == null || liveRoomId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        return userFollowService.cancelfollow(token.getId(), liveRoomId);
    }
    /**
     * 判断人员是否关注过没有
     */
    @RequestMapping(value = "/isFollowRoom.user")
    @ResponseBody
    @ApiOperation(value = "判断人员是否关注过没有", httpMethod = "GET", notes = "判断人员是否关注过没有")
    public ActResultDto isFollowRoom(HttpServletRequest request ,
                                     @ApiParam(required =true, name = "ID", value = "Id")  Long liveRoomId) {
        AppUserIdentity token = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();
        //判断参数
        if (liveRoomId == null || liveRoomId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        }else{
            Map map = new HashMap();
            if (userFollowService.isFollowRoom(liveRoomId,token.getId())){
                map.put("isFollow",1);
            }else {
                map.put("isFollow",0);
            }
            res.setData(map);
        }
        return res;
    }

}