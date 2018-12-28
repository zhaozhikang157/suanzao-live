package com.longlian.live.controller;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.service.MobileVersionService;
import com.longlian.model.Course;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/joinCourseRecord")
public class JoinCourseRecordController {
    private static Logger log = LoggerFactory.getLogger(JoinCourseRecordController.class);

    @Autowired
    JoinCourseRecordService joinCourseRecordService;

    @Autowired
    CourseService courseService;

    /**
     * 根据课程Id  获取当前用户报名的记录
     *
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping("/getJCRecordByCourseId.user")
    @ApiOperation(value = "根据课程Id获取当前用户报名的记录", httpMethod = "GET", notes = "根据课程Id获取当前用户报名的记录")
    public ActResultDto getJCRecordByCourseIdAndAppId(HttpServletRequest request,@ApiParam(required = true,name = "课程ID",value = "课程ID")long courseId) {
        ActResultDto actResultDto = new ActResultDto();
        AppUserIdentity identity = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Course c = courseService.getCourse(courseId);
        boolean isJoin = joinCourseRecordService.isJoinCourse(identity.getId(), c);
        String isJoinStatus = "0";
        if (isJoin) isJoinStatus = "1";
        Map data = new HashMap();
        data.put("mobile", identity.getMobile());
        data.put("isJoin", isJoinStatus);
        actResultDto.setData(data);
        return actResultDto;

    }

    /*当前用户推荐听课人数
    */
    @RequestMapping(value = "/getCourseRecordCount.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "当前用户推荐听课人数", httpMethod = "POST", notes = "当前用户推荐听课人数")
    public ActResultDto getCourseRecordCount(HttpServletRequest request) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            result.setData(joinCourseRecordService.getCourseRecordCount(token.getId()));
        }
        return result;
    }


    /**
     * 参加该课程的列表
     */
    @RequestMapping(value = "/getCourseRecordList.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "参加该课程的列表", httpMethod = "GET", notes = "参加该课程的列表")
    public ActResultDto getCourseRecordList(@ApiParam(required = true,name = "课程ID",value = "课程ID")Long id) throws Exception {
        ActResultDto result = new ActResultDto();
        if (id == null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
        Course course = courseService.getCourse(id);
            if(course.getSeriesCourseId()>0)
            {
                id= course.getSeriesCourseId();
            }
            result.setData(joinCourseRecordService.getCourseRecordList(id));
        }
        return result;
    }

    @Autowired
    MobileVersionService versionService;
    /**
     * 我买的课程
     **/
    @RequestMapping(value = "/getMyBuyCourseList.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "我买的课程", httpMethod = "GET", notes = "我买的课程")
    public ActResultDto getBuyCourseList(HttpServletRequest request, Integer pageNum, Integer pageSize) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
        List list = joinCourseRecordService.getMyBuyCourseList(token.getId(), pageNum, pageSize , isHaveRecord);
        if(list!=null && list.size()>0){
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        }else{
            result.setCode(ReturnMessageType.NO_DATA.getCode());
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        result.setData(list);
        return result;
    }

    /**
     * 我买的课程
     **/
    @RequestMapping(value = "/getMyBuyCourseListV2.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "我买的课程", httpMethod = "GET", notes = "我买的课程")
    public ActResultDto getMyBuyCourseListV2(HttpServletRequest request, Integer pageNum, Integer pageSize) throws Exception {
        ActResultDto result = new ActResultDto();
        boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List list = joinCourseRecordService.getMyBuyCourseListV2(token.getId(), pageNum, pageSize, isHaveRecord);
        if(list!=null && list.size()>0){
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        }else{
            result.setCode(ReturnMessageType.NO_DATA.getCode());
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        result.setData(list);
        return result;
    }

    /**
     * 我购买的课程 V1.6.4
     * @author :liu.na
     **/
    @RequestMapping(value = "/getMyBuyCourseListV3.user")
    @ResponseBody
    @ApiOperation(value = "我买的课程", notes = "我买的课程")
    public ActResultDto getMyBuyCourseListV3(HttpServletRequest request,String clientType, Integer pageNum, Integer pageSize) throws Exception {
        ActResultDto result = new ActResultDto();
        if(pageNum==null || pageSize==null){   //判断参数 不合法问题
            result.setCode(ReturnMessageType.PARAMETER_NULL_ERROR.getCode());
            result.setMessage(ReturnMessageType.PARAMETER_NULL_ERROR.getMessage());
            return result;
        }
        if(pageNum<=0 || pageSize<=0){  //判断参数值不合法问题
            result.setCode(ReturnMessageType.PARAMETER_VALUE_ERROR.getCode());
            result.setMessage(ReturnMessageType.PARAMETER_VALUE_ERROR.getMessage());
            return result;
        }
        boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> dataList = joinCourseRecordService.getMyBuyCourseListV3(token.getId(), pageNum, pageSize, isHaveRecord,clientType);
        if(dataList!=null){
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        }else{
            result.setCode(ReturnMessageType.NO_DATA.getCode());
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        result.setData(dataList);
        return result;
    }
}
