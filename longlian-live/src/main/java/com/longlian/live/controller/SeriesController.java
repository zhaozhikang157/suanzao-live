package com.longlian.live.controller;
import com.huaxin.util.UserAgentUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.dto.UserAgent;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.*;
import com.longlian.live.util.SystemUtil;
import com.longlian.model.Course;
import com.longlian.model.LiveRoom;
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

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/5/22.
 */
@Controller
@RequestMapping(value = "/series")
public class SeriesController{
    private static Logger log = LoggerFactory.getLogger(SeriesController.class);

    @Autowired
    CourseService courseService;
    @Autowired
    UpdateAndCreateCourseService updateAndCreateCourseService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    CourseRelayService courseRelayService;
    /**
     * 创建系列课
     **/
    @RequestMapping(value = "/createSeriesCourse.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "创建系列课", httpMethod = "POST", notes = "创建系列课")
    public ActResultDto createCourse(HttpServletRequest request, @ApiParam(required =true, name = "课程类对象", value = "课程类对象")CourseDto course) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        course.setAppId(token.getId());
        LiveRoom liveRoom =  liveRoomService.findByAppId(token.getId());
        if (course.getRoomId() == 0) {
            course.setRoomId(liveRoom.getId());
        }
        if(!Utility.isNullorEmpty(course.getLiveTopic())){
            course.setLiveTopic(SystemUtil.processQuotationMarks(course.getLiveTopic()));
        }
        ActResultDto actResultDto = new ActResultDto();

        //开课时间不能晚于当前时间前5分钟
        /*Date createTime = course.getStartTime();
        Calendar noww = Calendar.getInstance();
        noww.add(Calendar.MINUTE, -5);
        if(createTime.before(noww.getTime())){
            actResultDto.setCode(ReturnMessageType.COURSE_START_TIME_BEFORE_NOW.getCode());
            actResultDto.setMessage(ReturnMessageType.COURSE_START_TIME_BEFORE_NOW.getMessage());
            return actResultDto;
        }*/

        if("1".equals(liveRoom.getRoomStatus())){
            actResultDto.setCode(ReturnMessageType.ROOM_FORBIDDEN_NOT_CREATE.getCode());
            actResultDto.setMessage(ReturnMessageType.ROOM_FORBIDDEN_NOT_CREATE.getMessage());
            return actResultDto;
        }
        actResultDto = updateAndCreateCourseService.createSeriesCourse(course);
        return actResultDto;
    }

    /**
     * 系列课的修改
     **/
    @RequestMapping(value = "/updateSeriesCourse.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "系列课的修改", httpMethod = "POST", notes = "系列课的修改")
    public ActResultDto uploadCourse(HttpServletRequest request,@ApiParam(required =true, name = "课程类对象", value = "课程类对象") CourseDto course) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto ac = new ActResultDto();
        try {
            if (course.getId()==0){
                ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
                ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
                return ac;
            }
            if(!Utility.isNullorEmpty(course.getLiveTopic())){
                course.setLiveTopic(SystemUtil.processQuotationMarks(course.getLiveTopic()));
            }
            /*int seriesSingleCount = courseService.getSeriesSingleCourseBySeriesId(course.getId(),token.getId());
            if(course.getCoursePlanCount()<seriesSingleCount){
                ac.setCode(ReturnMessageType.COURSE_PLAN_NOT_LESS_CURRENT_COUNTS.getCode());
                ac.setMessage(ReturnMessageType.COURSE_PLAN_NOT_LESS_CURRENT_COUNTS.getMessage());
                return ac;
            }*/
            Course c = courseService.getCourse(course.getId());
            if("0".equals(course.getIsRelay())){
                course.setRelayCharge(c.getRelayCharge());
                course.setRelayScale(c.getRelayScale());
            }
            //如果开关关闭，不记录数据
            if(1!=Integer.valueOf(course.getIsRelay())){
                course.setRelayCharge(c.getRelayCharge());
                course.setRelayScale(c.getRelayScale());
                course.setSetRelayTime(c.getSetRelayTime());
            }else{
                course.setSetRelayTime(new Date());
            }
            if(c.getRelayCharge()==null){
                c.setRelayCharge(new BigDecimal(0));
            }
            if(1==c.getIsOpened() && 1==Integer.valueOf(course.getIsRelay())){
                if(c.getRelayCharge().compareTo(course.getRelayCharge())!=0 || course.getRelayScale()!=c.getRelayScale()){
                    ac.setCode(ReturnMessageType.CAN_NOTRELAY_COURSE_UPDATE.getCode());
                    ac.setMessage(ReturnMessageType.CAN_NOTRELAY_COURSE_UPDATE.getMessage());
                    return ac;
                }
            }
            if(1!=c.getIsOpened() && course.getIsRelay()==1){
                courseService.updateIsOpenById(1,course.getId());
            }
            if (c==null ||token.getId() != c.getAppId()){////判断直播间是否是当前用户的
                ac.setCode(ReturnMessageType.COURSE_NOT_MODIFY.getCode());
                ac.setMessage(ReturnMessageType.COURSE_NOT_MODIFY.getMessage());
            } else{
                updateAndCreateCourseService.updateSeriesCourse(course, request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ac.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            ac.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        return ac;
    }

    /**
     * 我的系列课信息
     * @param request
     * @param 
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getSeriesCourseInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "我的系列课信息", httpMethod = "GET", notes = "我的系列课信息")
    public ActResultDto getSeriesCourseInfo(HttpServletRequest request,@ApiParam(required =true, name = "ID", value = "ID") Long seriesid) throws Exception {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        String v = request.getParameter("v");
        long appid = 0;
        if (token != null) {
            appid = token.getId();
        }
        ActResultDto ac = null ;
        if(seriesid != null && seriesid > 0){
            ac = courseService.getSeriesCourseInfo(seriesid, appid , UserAgentUtil.isWechatClient(request),v);
            if (token != null) {
                ac.setExt(token.getMobile());
            }
        }else{
            ac = new ActResultDto();
        }
        return ac;
    }

    /**
     * 我的系列课信息
     * @param request
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getSeriesCourseInfo.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "我的系列课信息", httpMethod = "GET", notes = "我的系列课信息")
    public ActResultDto getSeriesCourseInfoUser(HttpServletRequest request,@ApiParam(required =true, name = "ID", value = "ID")  Long seriesid) throws Exception {
        return  getSeriesCourseInfo(  request,   seriesid) ;
    }
    @Autowired
    MobileVersionService versionService;
    @Autowired
    MsgCancelService msgCancelService;

    /**
     * 我的课程
     **/
    @RequestMapping(value = "/getMyCourseList", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "我的课程", httpMethod = "GET", notes = "我的课程")
    public ActResultDto getBuyCourseList(HttpServletRequest request, Long seriesid,
                                        Integer offset,
                                        Integer pageSize) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        long appid = 0;
        if (token != null) {
            appid = token.getId();
        }
        if (seriesid == null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        }else{
            boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
            boolean isOldVersion = false;
            if(!UserAgentUtil.isWechatClient(request)){
                isOldVersion = versionService.lessThan160(request);
            }
            List<Map> list=null;
//            if(String.valueOf(seriesid).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
//                CourseRelayDto courseRelayDto = courseRelayService.queryById(seriesid);
//                list = courseService.getMyRelayCourseList(courseRelayDto.getOriCourseId(),appid,offset, pageSize , isHaveRecord , isOldVersion);
//            }else {
                list = courseService.getMyCourseList(seriesid,appid,offset, pageSize , isHaveRecord , isOldVersion);
//            }

            UserAgent ua =  UserAgentUtil.getUserAgentCustomer(request);
            if (ua!=null&&UserAgentUtil.ios.equals(ua.getCustomerType())) {
              if(list!=null&&list.size()>0){
                for(Map m:list){
                  if(m.get("visitCount")!=null){
                     m.put("joinCount",m.get("visitCount").toString());
                  }
              }
            }

            }
            if (list != null && list.size() > 0) {

                if (list.size() > 0) {
                    for (Object o : list) {
                        Map map2 = (Map)o;
                        String msgCancels = "";
                        try {
                            msgCancels = msgCancelService.findMsgCancel(Long.parseLong(map2.get("id").toString()));
                        }catch (Exception e){
                            msgCancels = "";
                        }
                        map2.put("msgCancelId",msgCancels);
                    }
                }

                result.setData(list);
                result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            } else {
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
                result.setCode(ReturnMessageType.NO_DATA.getCode());
            }
        }
        return result;
    }

    /**
     * 系列课里面的单节课类型（倒排第一个）
     **/
    @RequestMapping(value = "/getLastCourseType.user")
    @ResponseBody
    @ApiOperation(value = "系列课里面的单节课类型（倒排第一个）", httpMethod = "GET", notes = "系列课里面的单节课类型（倒排第一个）")
    public ActResultDto getLastCourseType(HttpServletRequest request,@ApiParam(required =true, name = "ID", value = "ID") Long seriesid) throws Exception {
        ActResultDto result = new ActResultDto();
        if (seriesid == null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        }else{
            String type = courseService.getLastCourseType(seriesid);
            result.setData(type);
        }
        return result;
    }

    /**
     * 我的课程
     **/
    @RequestMapping(value = "/getMyCourseList.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "我的课程", httpMethod = "GET", notes = "我的课程")
    public ActResultDto getBuyCourseListUser(HttpServletRequest request,@ApiParam(required =true, name = "ID", value = "ID") Long seriesid,
                                             @ApiParam(required =true, name = "分页偏移量", value = "分页偏移量") Integer offset,
                                             @ApiParam(required =true, name = "每页数量", value = "每页数量") Integer pageSize) throws Exception {
        return  getBuyCourseList( request, seriesid, offset,  pageSize) ;
    }
    /**
     * 判断系列课排课计划是否已达上限
     **/
    @RequestMapping(value = "/limitedCoursePlanCount", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "判断系列课排课计划是否已达上限", httpMethod = "GET", notes = "判断系列课排课计划是否已达上限")
    public ActResultDto limitedCoursePlanCount(HttpServletRequest request,@ApiParam(required =true, name = "ID", value = "ID") Long seriesid) throws Exception {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        long appid = 0;
        if (token != null) {
            appid = token.getId();
        }
        ActResultDto ac = null ;
        ac =courseService.limitedCoursePlanCount(seriesid,appid);
        return ac;
    }

    /**
     * 判断系列课排课计划是否已达上限
     **/
    @RequestMapping(value = "/limitedCoursePlanCount.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "判断系列课排课计划是否已达上限", httpMethod = "GET", notes = "判断系列课排课计划是否已达上限")
    public ActResultDto limitedCoursePlanCountUser(HttpServletRequest request,@ApiParam(required =true, name = "ID", value = "ID") Long seriesid) throws Exception {
        return limitedCoursePlanCount( request, seriesid) ;
    }
}
