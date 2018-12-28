package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseManagerService;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by admin on 2017/7/10.
 */
@RequestMapping("/courseManager")
@Controller
public class CourseManagerController {
    private static Logger log = LoggerFactory.getLogger(CourseManagerController.class);

    @Autowired
    CourseManagerService courseManagerService;

    /**
     * 获取老师直播间的管理人员
     * @param request
     * @param pageSize
     * @param offset
     * @return
     */
    @RequestMapping("/findAllManagers.user")
    @ResponseBody
    @ApiOperation(value = "获取老师直播间的管理人员", httpMethod = "GET", notes = "获取老师直播间的管理人员")
    public ActResultDto findAllManagers(HttpServletRequest request , Integer pageSize , Integer offset){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(pageSize == null) pageSize = 10;
        if(offset == null) offset = 0;
        return courseManagerService.findAllManagersPage(token.getId(),pageSize,offset);
    }

    /**
     * 根据userId,查找该用户信息
     * @param request
     * @param userId
     * @return
     */
    @RequestMapping("/findAppUserById.user")
    @ResponseBody
    @ApiOperation(value = "查找该用户信息", httpMethod = "GET", notes = "查找该用户信息")
    public ActResultDto findAppUserById(HttpServletRequest request ,
                                        @ApiParam(required = true,name = "用户ID",value = "用户ID")String userId,
                                        Integer pageSize , Integer offset ){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = new ActResultDto();
        if(StringUtils.isEmpty(userId)){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        resultDto = courseManagerService.findAppUserById(Long.valueOf(userId), pageSize,
                offset, token.getId());
        return resultDto;
    }

    /**
     * 添加管理员
     * @param request
     * @param userId
     * @return
     */
    @RequestMapping("/createCourseManager.user")
    @ResponseBody
    @ApiOperation(value = "添加管理员", httpMethod = "GET", notes = "添加管理员")
    public ActResultDto createCourseManager(HttpServletRequest request ,
                                            @ApiParam(required = true,name = "用户ID",value = "用户ID")String userId){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = new ActResultDto();
        if(StringUtils.isEmpty(userId)){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        if(userId.equals(token.getId() + "")){
            resultDto.setCode(ReturnMessageType.NOT_USE_SELF_MANAGER.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_USE_SELF_MANAGER.getMessage());
            return resultDto;
        }
        resultDto = courseManagerService.createCourseManager(token.getId(), Long.valueOf(userId));
        return resultDto;
    }


    /**
     * 删除管理员
     * @param request
     * @param id
     * @return
     */
    @RequestMapping("/delCourseManagerById.user")
    @ResponseBody
    @ApiOperation(value = "删除管理员", httpMethod = "GET", notes = "删除管理员")
    public ActResultDto delCourseManagerById(HttpServletRequest request ,
                                             @ApiParam(required = true,name = "记录ID",value = "记录ID")Long id){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = new ActResultDto();
        if(id == null || id <1){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        resultDto = courseManagerService.delCourseManagerById(id,token.getId());
        return resultDto;
    }

    /**
     * 删除课程的管理人员
     * @param courseId
     * @param userId
     * @return
     */
    @RequestMapping("/delManagerRealById.user")
    @ResponseBody
    @ApiOperation(value = "删除课程的管理人员", httpMethod = "GET", notes = "删除课程的管理人员")
    public ActResultDto delManagerRealById(@ApiParam(required = true,name = "课程id",value = "课程ID")Long courseId ,
                                           @ApiParam(required = true,name = "用户ID",value = "用户ID")Long userId , HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = new ActResultDto();
        if(courseId == null || courseId <1 ){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            log.info("参数课程id为空");
            return resultDto;
        }
        if(userId == null || userId <1 ){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            log.info("参数课程userId为空");
            return resultDto;
        }
        resultDto = courseManagerService.delManagerRealById(courseId, userId, token.getId());
        return resultDto;
    }

    /**
     * 添加课程的管理人员
     * @param courseId
     * @param userId
     * @return
     */
    @RequestMapping("/createManagerReal.user")
    @ResponseBody
    @ApiOperation(value = "添加课程的管理人员", httpMethod = "GET", notes = "添加课程的管理人员")
    public ActResultDto createManagerReal(HttpServletRequest request,
                                         Long courseId ,
                                          Long userId){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = new ActResultDto();
        if(courseId == null || courseId <1 ){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        if(userId == null || userId <1 ){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        if(userId.equals(token.getId()+"")){
            resultDto.setCode(ReturnMessageType.NOT_USE_SELF_MANAGER.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_USE_SELF_MANAGER.getMessage());
            return resultDto;
        }
        return courseManagerService.createManagerReal(courseId,userId,token.getId());
    }

}
