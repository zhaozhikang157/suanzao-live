package com.longlian.live.controller;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.CourseService;
import com.longlian.live.service.LiveChannelService;
import com.longlian.live.service.LiveConnectService;
import com.longlian.live.service.MixLiveService;
import com.longlian.model.Course;
import com.longlian.model.LiveConnectRequest;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ConnectStatus;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = "/")
public class LiveConnectController {

    private static Logger log = LoggerFactory.getLogger(LiveConnectController.class);

    @Autowired
    LiveConnectService liveConnectService;
    @Autowired
    CourseService courseService;
    @Autowired
    LiveChannelService qiNiuliveChannelService;
    @Autowired
    MixLiveService mixLiveService;
    /**
     * 申请列表
     * @param request
     * @param courseId
     * @param offset
     * @param pageSize
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "liveConnect/getEvenForWheatListPage.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "申请连麦列表", httpMethod = "GET", notes = "申请连麦列表")
    public ActResultDto getEvenForWheatListPage( @ApiParam(required = true,name = "课程ID",value = "课程ID")Optional<Long> courseId,
            HttpServletRequest request,Optional<Integer> offset, Optional<Integer> pageSize,Optional<Long> student) throws Exception {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        return liveConnectService.getEvenForWheatListPage(offset, pageSize, Optional.of(token.getId()), student, courseId);
    }
    /**
     * 最近连麦
     * @param request
     * @param courseId
     * @param offset
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "liveConnect/getEvenForWheatEndListPage.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "最近连麦", httpMethod = "GET", notes = "最近连麦")
    public ActResultDto getEvenForWheatEndListPage(@ApiParam(required = true,name = "课程ID",value = "课程ID")Optional<Long> courseId,
                                             HttpServletRequest request,Optional<Integer> offset, Optional<Integer> pageSize,Optional<Long> student) throws Exception {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        return liveConnectService.getEvenForWheatEndListPage(offset, pageSize, Optional.of(token.getId()), student, courseId);

    }

    /**
     * 修改连麦状态
     * @param applyUser 申请人ID
     * @param courseId  课程ID
     * @param status    状态
     * @param msg       失败原因
     * @return
     */
    @RequestMapping("liveConnect/updateConnectStatus.user")
    @ResponseBody
    public ActResultDto updateConnectStatus(Long courseId , Long applyUser, String status , String msg){
        ActResultDto resultDto = new ActResultDto();
        if(courseId == null || courseId < 1){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto ;
        }
        if(applyUser == null || applyUser < 1){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto ;
        }
        if(StringUtils.isEmpty(status)){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto ;
        }
        if(ConnectStatus.connect_fail.getValue().equals(status)){
            if(StringUtils.isEmpty(msg)){
                resultDto.setCode(ReturnMessageType.CONNECT_ERROR_MSG.getCode());
                resultDto.setMessage(ReturnMessageType.CONNECT_ERROR_MSG.getMessage());
                return resultDto ;
            }
        }else{
            if(StringUtils.isEmpty(msg)){
                msg = "";
            }

        }
        return liveConnectService.updateStatusV2(courseId, applyUser, status, msg);
    }


    /**
     * 关闭连麦
     * @param courseId  课程ID
     * @return
     */
    @RequestMapping("liveConnect/close.user")
    @ResponseBody
    public ActResultDto close(Long courseId , Long nowConnectedId){
        ActResultDto resultDto = new ActResultDto();
        Course course = courseService.getCourseFromRedis(courseId);
        LiveConnectRequest liveConnectRequest = new LiveConnectRequest();
        liveConnectRequest.setStudent(nowConnectedId);
        liveConnectRequest.setTeacher(course.getAppId());
        liveConnectRequest.setCourseId(courseId);
        liveConnectRequest.setCloseTime(new Date());
        liveConnectRequest.setStatus(ConnectStatus.disconnected.getValue());
        liveConnectRequest.setFailMsg(ConnectStatus.disconnected.getText());
        mixLiveService.closeConnectRequest(liveConnectRequest);
        return resultDto;
    }






    /**
     * 老师发送连麦申请
     * @param request
     * @param courseId
     * @param toUser
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "teacher/applyConnect.user")
    @ResponseBody
    @ApiOperation(value = "老师发送连麦申请", httpMethod = "GET", notes = "老师发送连麦申请")
    public ActResultDto teacherApplyConnect(HttpServletRequest request ,
                                            Long courseId,
                                            Long toUser , Long nowConnectedId) throws Exception {
        //移出老流
        //保存数据库 状态是：0
        AppUserIdentity ui = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);

//        Map roomToken = qiNiuliveChannelService.getRoomToken(courseId , ui.getId());


        try {
            Course course = courseService.getCourseFromRedis(courseId);

            //先断开nowConnectedId
            if (nowConnectedId != null && nowConnectedId != 0) {
                LiveConnectRequest liveConnectRequest = new LiveConnectRequest();
                liveConnectRequest.setStudent(nowConnectedId);
                liveConnectRequest.setTeacher(course.getAppId());
                liveConnectRequest.setCourseId(courseId);
                liveConnectRequest.setCloseTime(new Date());
                liveConnectRequest.setStatus(ConnectStatus.disconnected.getValue());
                mixLiveService.closeConnectRequest(liveConnectRequest);
            }

            //插入新的正在连接
            LiveConnectRequest liveConnectRequest = new LiveConnectRequest();
            liveConnectRequest.setStudentStatus("1");
            liveConnectRequest.setStudent(toUser);
            liveConnectRequest.setApplyUser(course.getAppId());
            liveConnectRequest.setTeacher(course.getAppId());
            liveConnectRequest.setAgreeStatus("0");
            liveConnectRequest.setCourseId(courseId);
            liveConnectRequest.setApplyTime(new Date());
            liveConnectRequest.setStatus(ConnectStatus.applying.getValue());
            liveConnectRequest.setLoseTime(20L);
            mixLiveService.saveLiveConnect(liveConnectRequest);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{}" , ex);
        }


        return ActResultDto.success();

    }

    /**
     * 同意连麦申请
     * @param request
     * @param courseId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "liveConnect/agreeConnect.user")
    @ResponseBody
    @ApiOperation(value = "同意连麦申请", httpMethod = "GET", notes = "同意连麦申请")
    public ActResultDto studentAgreeConnect(HttpServletRequest request ,
                                            Long courseId , Long nowConnectedId) throws Exception {
        AppUserIdentity ui = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);

//        Map roomToken = qiNiuliveChannelService.getRoomToken(courseId , ui.getId());


        try {
            //插入新的正在连接
            //LiveConnectRequest liveConnectRequest = new LiveConnectRequest();
            Course course = courseService.getCourseFromRedis(courseId);
            //liveConnectRequest.setStudentStatus("0");
            //mixLiveService.saveLiveConnect(liveConnectRequest);

            //先断开nowConnectedId
            if (nowConnectedId != null && nowConnectedId != 0) {
                LiveConnectRequest liveConnectRequest = new LiveConnectRequest();
                liveConnectRequest.setStudent(nowConnectedId);
                liveConnectRequest.setTeacher(course.getAppId());
                liveConnectRequest.setCourseId(courseId);
                liveConnectRequest.setCloseTime(new Date());
                liveConnectRequest.setStatus(ConnectStatus.disconnected.getValue());
                mixLiveService.closeConnectRequest(liveConnectRequest);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{}" , ex);
        }

        return ActResultDto.success();

    }

    /**
     * 学生发送连麦申请
     * @param request
     * @param courseId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "student/applyConnect.user")
    @ResponseBody
    @ApiOperation(value = "学生发送连麦申请", httpMethod = "GET", notes = "学生发送连麦申请")
    public ActResultDto studentApplyConnect(HttpServletRequest request ,
                                            Long courseId) throws Exception {
        //移出老流
        AppUserIdentity ui = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
//        Map roomToken = qiNiuliveChannelService.getRoomToken(courseId , ui.getId());

        try {
            //插入新的正在连接
            LiveConnectRequest liveConnectRequest = new LiveConnectRequest();
            Course course = courseService.getCourseFromRedis(courseId);
            //liveConnectRequest.setStudentStatus("0");
            liveConnectRequest.setStudent(ui.getId());
            liveConnectRequest.setApplyUser(ui.getId());
            liveConnectRequest.setTeacher(course.getAppId());
            liveConnectRequest.setAgreeStatus("1");
            liveConnectRequest.setCourseId(courseId);
            liveConnectRequest.setApplyTime(new Date());
            liveConnectRequest.setStatus(ConnectStatus.applying.getValue());
            liveConnectRequest.setStudentStatus("1");
            liveConnectRequest.setLoseTime(10 * 60l);
            mixLiveService.saveLiveConnect(liveConnectRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{}" , ex);
        }
        return ActResultDto.success();
    }

    /**
     * 取得可连麦用户列表
     **/
    @RequestMapping(value = "liveConnect/getAllCanConnectUsers.user")
    @ResponseBody
    @ApiOperation(value = "取得更多用户列表", httpMethod = "GET", notes = "取得更多用户列表")
    public ActResultDto getAllUsers(HttpServletRequest request ,
                                    Long courseId,
                                    String ids , Long nowConnectedId, Integer pageSize) throws Exception {

        //判断参数
        ActResultDto res = new ActResultDto();
        if (courseId == null || courseId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        return mixLiveService.getAllUsers(courseId, ids, pageSize, nowConnectedId);
    }

    /**
     * 获取直播间roomToken
     * @param request
     * @param courseId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "liveConnect/getRoomToken.user")
    @ResponseBody
    @ApiOperation(value = "取roomToken",httpMethod = "GET",notes = "取RoomToken")
    public ActResultDto getRoomToken(HttpServletRequest request , Long courseId) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity ui = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(ui == null){
            resultDto.setCode(ReturnMessageType.ERROR_403.getCode());
            resultDto.setMessage(ReturnMessageType.ERROR_403.getMessage());
            return resultDto;
        }
        if(courseId == null || courseId < 1){
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
        Map roomToken = qiNiuliveChannelService.getRoomToken(courseId , ui.getId());
        if(roomToken == null){
            resultDto.setCode(ReturnMessageType.GET_ROOM_TOKEN_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.GET_ROOM_TOKEN_ERROR.getMessage());
            return resultDto;
        }
        resultDto.setData(roomToken);
        return resultDto;
    }
}
