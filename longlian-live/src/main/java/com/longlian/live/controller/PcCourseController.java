package com.longlian.live.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.UserAgentUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.dto.UserAgent;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.SpringContextUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.live.service.*;
import com.longlian.live.task.CreateRelayCourse;
import com.longlian.live.util.PicUtil;
import com.longlian.live.util.SystemUtil;
import com.longlian.live.util.guava.F;
import com.longlian.model.*;
import com.longlian.model.course.CourseCard;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.OssBucket;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/29.
 */
@Controller
@RequestMapping(value = "/pcCourse")
public class PcCourseController {
    private static Logger log = LoggerFactory.getLogger(PcCourseController.class);

    @Autowired
    CourseService courseService;

    @Autowired
    UpdateAndCreateCourseService updateAndCreateCourseService;

    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    PicUtil picUtil;
    @Autowired
    CourseAuditService courseAuditService;
    @Autowired
    AppMsgService appMsgService;

    @RequestMapping("/index.user")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/func/pc/courseList");
        return view;
    }

    /**
     * 课程列表
     */
    @RequestMapping(value = "/getList.user")
    @ResponseBody
    @ApiOperation(value = "课程列表", httpMethod = "GET", notes = "课程列表")
    public DatagridResponseModel getListPage(DataGridPage requestModel, HttpServletRequest request, @ApiParam(required =true, name = "查询条件 map格式", value = "查询条件 map格式") @RequestParam Map map) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        DatagridResponseModel responseModel = new DatagridResponseModel();
        map.put("appId", token.getId());
        requestModel.setPageSize(20);
        responseModel.setRows(courseService.getPcCourseListPage(requestModel, map));
        responseModel.setTotal(requestModel.getTotalCount());
        return responseModel;
    }

    //创建单节课页面
    @RequestMapping("/toCreateSingleCourse.user")
    @ApiOperation(value = "创建单节课页面", httpMethod = "GET", notes = "创建单节课页面")
    public ModelAndView toCreateSingleCourse(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("/func/pc/createSingleCourse");
        view.addObject("baseUrl", OssBucket.longlian_input.getBucketHttpsAddress());
        return view;
    }

    //创建单节课页面
    @RequestMapping("/toCreateSeriesCourse.user")
    public ModelAndView toCreateSeriesCourse() {
        ModelAndView view = new ModelAndView("/func/pc/createSeriesCourse");
        return view;
    }

    @RequestMapping(value = "/getSeriesList.user")
    @ResponseBody
    @ApiOperation(value = "查询用户课程", httpMethod = "GET", notes = "查询用户课程")
    public ActResultDto getSeriesList(HttpServletRequest request) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> seriesList = courseService.getAllPcSeriesClass(token.getId());
        ActResultDto actResultDto = new ActResultDto();
        actResultDto.setData(seriesList);
        return actResultDto;
    }

    @RequestMapping(value = "/getSeriesCourse.user")
    @ResponseBody
    @ApiOperation(value = "查询单节课程", httpMethod = "GET", notes = "查询单节课程")
    public ActResultDto getSeriesCourse(HttpServletRequest request,@ApiParam(required =true, name = "课程ID", value = "课程ID") @RequestParam Long courseId) throws Exception {
        Course course = courseService.getCourse(courseId);
        ActResultDto actResultDto = new ActResultDto();
        actResultDto.setData(course);
        return actResultDto;
    }

    /**
     * 创建单节课
     **/
    @RequestMapping(value = "/createPcCourse.user")
    @ResponseBody
    @ApiOperation(value = "创建单节课", httpMethod = "GET", notes = "创建单节课")
    public ActResultDto createPcCourse(HttpServletRequest request,@ApiParam(required =true, name = "课程类对象", value = "课程类对象") @RequestBody CourseDto course) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        course.setAppId(token.getId());
        UserAgent userAgent = UserAgentUtil.getUserAgentCustomer(request);
        boolean isWeixin = false;
        if (userAgent != null && UserAgentUtil.weixin.equals(userAgent.getCustomerType())) {
            isWeixin = true;
        }
        if (course.getRoomId() == 0) {
            LiveRoom liveRoom = liveRoomService.findByAppId(token.getId());
            course.setRoomId(liveRoom.getId());
            course.setAutoCloseTime(liveRoom.getAutoCloseTime());
        }else{
            LiveRoom liveRoom = liveRoomService.findById(course.getRoomId());
            course.setAutoCloseTime(liveRoom.getAutoCloseTime());
        }

        if(!Utility.isNullorEmpty(course.getLiveTopic())){
            course.setLiveTopic(SystemUtil.processQuotationMarks(course.getLiveTopic()));
        }
        course.setCoverssAddress(picUtil.base64ToUrl(course.getCoverssAddress()));
        course.setIsRecorded("1");
        String topic = course.getLiveTopic();
        course.setLiveTopic(topic.replace("\"", "“"));
        ActResultDto actResultDto = updateAndCreateCourseService.createCourse(course);
        //开启另一个线程创建转播课程（系列单节课）
        if(course.getSeriesCourseId() > 0){
            CreateRelayCourse createRelayCourse = SpringContextUtil.getBeanByType(CreateRelayCourse.class);
            createRelayCourse.setAppId(token.getId());
            createRelayCourse.setCourseId(course.getId());
            createRelayCourse.setRoomId(course.getRoomId());
            F.submit(createRelayCourse);
        }
        return actResultDto;
    }

    /**
     * 创建系列课
     **/
    @RequestMapping(value = "/createPcSreiesCourse.user")
    @ResponseBody
    @ApiOperation(value = "创建系列课", httpMethod = "GET", notes = "创建系列课")
    public ActResultDto createPcSreiesCourse(HttpServletRequest request,@ApiParam(required =true, name = "课程类对象", value = "课程类对象") @RequestBody CourseDto course) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        course.setAppId(token.getId());
        if (course.getRoomId() == 0) {
            LiveRoom liveRoom = liveRoomService.findByAppId(token.getId());
            course.setRoomId(liveRoom.getId());
        }
        if(!Utility.isNullorEmpty(course.getLiveTopic())){
            course.setLiveTopic(SystemUtil.processQuotationMarks(course.getLiveTopic()));
        }
        course.setCoverssAddress(picUtil.base64ToUrl(course.getCoverssAddress()));
        course.setIsRecorded("1");
        String topic = course.getLiveTopic();
        course.setLiveTopic(topic.replace("\"","“"));
        ActResultDto actResultDto = updateAndCreateCourseService.createSeriesCourse(course);
        //初始化审核记录
        /*CourseAudit courseAudit = new CourseAudit();
        courseAudit.setStatus("0");
        courseAudit.setCourseId(course.getId());
        courseAudit.setCreateTime(new Date());
        courseAuditService.insert(courseAudit);*/
        return actResultDto;
    }

    /**
     * 编辑
     */
    @RequestMapping("/toEditCourse")
    @ApiOperation(value = "编辑", httpMethod = "GET", notes = "编辑")
    public ModelAndView toEditCourse(@ApiParam(required =true, name = "课程ID", value = "课程ID") Long id) {
        //判断当前课(单课或系列课)是否添加白名单
        int result = courseService.findCourseIsExist(id);
        CourseCard courseCard = courseService.findCardUrlByCourseId(id);
        ModelAndView view = new ModelAndView("/func/pc/editSingle");
        view.addObject("baseUrl", OssBucket.longlian_input.getBucketHttpsAddress());
        Course course = courseService.getCourse(id);
        if (!Utility.isNullorEmpty(course)) {
            if ("1".equals(course.getIsSeriesCourse())) {
                view = new ModelAndView("/func/pc/editSeries");
            }
        }
        view.addObject("id", id);
        view.addObject("isPrivateCard", result);
        if(courseCard == null){
            courseCard = new CourseCard();
        }
        view.addObject("courseCard", courseCard);
        return view;
    }

    /**
     * 根据id进行查询
     */
    @RequestMapping(value = "/findByIdForEdit", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据id进行查询课程", httpMethod = "GET", notes = "根据id进行查询课程")
    public ActResult findByIdForEdit(@ApiParam(required =true, name = "课程ID", value = "课程ID")  long id) {
        ActResult result = new ActResult();
        Map map = new HashMap<>();
        String seriesCourseName = "";
        BigDecimal seriesChargeAmt = new BigDecimal(0);
        Course course = courseService.getCourse(id);
        if (course.getSeriesCourseId() > 0) {
            Course series = courseService.getCourse(course.getSeriesCourseId());
            seriesCourseName = series.getLiveTopic();
            seriesChargeAmt = series.getChargeAmt();
        }
        map.put("seriesCourseName", seriesCourseName);
        map.put("seriesChargeAmt", seriesChargeAmt);
        map.put("course", course);
        result.setData(map);
        return result;
    }

    /**
     * 编辑单节课
     */
    @RequestMapping(value = "/uploadPcCourse.user")
    @ResponseBody
    @ApiOperation(value = "编辑单节课", httpMethod = "GET", notes = "编辑单节课")
    public ActResultDto uploadCourse(HttpServletRequest request,@ApiParam(required =true, name = "课程类对象", value = "课程类对象")  @RequestBody CourseDto course,CourseCard courseCard) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto ac = new ActResultDto();
        try {
            if (course.getId() == 0) {
                ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
                ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
                return ac;
            }
            if(!Utility.isNullorEmpty(course.getLiveTopic())){
                course.setLiveTopic(SystemUtil.processQuotationMarks(course.getLiveTopic()));
            }
            Course c = courseService.getCourse(course.getId());
            if (c == null || token.getId() != c.getAppId()) {////判断直播间是否是当前用户的
                ac.setCode(ReturnMessageType.COURSE_NOT_MODIFY.getCode());
                ac.setMessage(ReturnMessageType.COURSE_NOT_MODIFY.getMessage());
            } else {
                if (!Utility.isNullorEmpty(course.getCoverssAddress())) {
                    course.setCoverssAddress(picUtil.base64ToUrl(course.getCoverssAddress()));
                }
//                course =  decideIsNull(course,c);
                //默认设置为录播的课\只要一修改就让它下线，mq去签别是否有敏感信息后再上线
                course.setIsRecorded("1");

                course.setHlsLiveAddress(course.getVideoAddress());
                course.setLiveAddress(course.getVideoAddress());
                course.setPushAddress(course.getVideoAddress());
                String topic = course.getLiveTopic();
                course.setLiveTopic(topic.replace("\"","“"));
                updateAndCreateCourseService.updateCourse(course,request);
                getPrivateCard(course, courseCard, ac, request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ac.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            ac.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        return ac;
    }
    @RequestMapping(value = "/getPreviewCourseCard")
    @ResponseBody
    public ActResultDto getPreviewCourseCard(HttpServletRequest request,CourseCard courseCard){
        ActResultDto ac = new ActResultDto();
        try {
            String url = courseService.doPreviewCourseCard(courseCard, request);
            ac.setData(url);
            ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        }
        return ac;
    }

    /**
     * 系列课的修改
     **/
    @RequestMapping(value = "/updatePcSeriesCourse.user")
    @ResponseBody
    @ApiOperation(value = "系列课的修改", httpMethod = "GET", notes = "系列课的修改")
    public ActResultDto updateSeriesCourse(HttpServletRequest request,@ApiParam(required =true, name = "课程类对象", value = "课程类对象")  @RequestBody CourseDto course,CourseCard courseCard) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto ac = new ActResultDto();
        try {
            if (course.getId() == 0) {
                ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
                ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
                return ac;
            }
            if(!Utility.isNullorEmpty(course.getLiveTopic())){
                course.setLiveTopic(SystemUtil.processQuotationMarks(course.getLiveTopic()));
            }
            Course c = courseService.getCourse(course.getId());
            if (c == null || token.getId() != c.getAppId()) {////判断直播间是否是当前用户的
                ac.setCode(ReturnMessageType.COURSE_NOT_MODIFY.getCode());
                ac.setMessage(ReturnMessageType.COURSE_NOT_MODIFY.getMessage());
            } else {
                if (!Utility.isNullorEmpty(course.getCoverssAddress())) {
                    course.setCoverssAddress(picUtil.base64ToUrl(course.getCoverssAddress()));
                }
//                course =  decideIsNull(course,c);
                //默认设置为录播的课\只要一修改就让它下线，mq去签别是否有敏感信息后再上线
                course.setIsRecorded("1");
                String topic = course.getLiveTopic();
                course.setLiveTopic(topic.replace("\"","“"));
                updateAndCreateCourseService.updateSeriesCourse(course,request);
                getPrivateCard(course, courseCard, ac, request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ac.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            ac.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        return ac;
    }

    /**
     * 抽取公共方法:获取个人邀请卡
     * @param course
     * @param courseCard
     * @param ac
     * @param request
     * @return
     */
    public void getPrivateCard(CourseDto course, CourseCard courseCard, ActResultDto ac, HttpServletRequest request){
        String modelUrl = course.getModelUrl();
        int rs = 0;
        if(StringUtils.isNotEmpty(modelUrl)){
            Long courseId = course.getId();
            if(courseId != null) {
                courseCard.setModelUrl(modelUrl);
                courseCard.setCardUrl(course.getCardUrl());
                courseCard.setCourseId(courseId);
                CourseCard result = courseService.findCardUrlByCourseId(courseId);
                if(result == null) {
                    rs = courseService.insertCourseCard(courseCard, request);
                } else {
                    rs = courseService.updateCourseCard(courseCard, request);
                }
                ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            }
        }
    }
    /**
     * 获取课件
     */
    @RequestMapping(value = "/getCourseWarelist")
    @ResponseBody
    @ApiOperation(value = "获取课件", httpMethod = "GET", notes = "获取课件")
    public ActResult getCourseWarelist(@ApiParam(required =true, name = "课件ID", value = "课件ID")  long id) {
        ActResult result = new ActResult();
        List<CourseWare> courseWareList = courseService.getCourseWare(id);
        result.setData(courseWareList);
        return result;
    }

    /**
     * 获取简介图片
     */
    @RequestMapping(value = "/getCourseImglist", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取简介图片", httpMethod = "GET", notes = "获取简介图片")
    public ActResult getCourseImglist(@ApiParam(required =true, name = "ID", value = "ID") long id) {
        ActResult result = new ActResult();
        List<CourseImg> CourseImglist = courseService.getCoursImg(id);
        result.setData(CourseImglist);
        return result;
    }
    @Autowired
    CourseBaseService courseBaseService;
    /**
     * 上线
     */
    @RequestMapping(value = "/updateUp", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "课程上架", httpMethod = "GET", notes = "课程上架")
    public ActResult updateUp(HttpServletRequest request,@ApiParam(required =true, name = "课程ID", value = "课程ID")  Long id) throws Exception {
        ActResult result = new ActResult();
        try {
            AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            courseService.updateUp(id, token.getId(), token.getName());
            Course course = courseBaseService.getCourseFromRedis(id);
            if (course != null) {
                appMsgService.updateMsgCourseStatus(id, "0", course.getRoomId());
            }
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("上架失败");
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/del.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除课程", httpMethod = "GET", notes = "删除课程")
    public ActResult del(HttpServletRequest request,@ApiParam(required =true, name = "课程ID", value = "课程ID") long id) throws Exception {
        ActResult result = new ActResult();
        try {
            AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            courseService.del(id, token.getId(), token.getName());
            redisUtil.del(RedisKey.course_live_weekly_selection_id );
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("操作失败");
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }

    @RequestMapping(value = "/getCourseStatus", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取课程状态", httpMethod = "GET", notes = "获取课程状态")
    public ActResult getCourseStatus(HttpServletRequest request,@ApiParam(required =true, name = "课程ID", value = "课程ID")  Long courseid) throws Exception {
        ActResult result = new ActResult();
        String bool = courseService.getCourseStatusByCourseId(courseid);
        result.setData(bool);
        return result;
    }

    //设置分享时长页面
    @RequestMapping("/toSetShareTime.user")
    @ApiOperation(value = "设置分享时长页面", httpMethod = "GET", notes = "设置分享时长页面")
    public ModelAndView toSetShareTime(@ApiParam(required =true, name = "课程ID", value = "课程ID") long id) {
        ModelAndView view = new ModelAndView("/func/pc/toSetShareTime");
        view.addObject("id", id);
        return view;
    }

    /**
     * 设置分享时长
     */
    @RequestMapping(value = "/setShareTime.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "设置分享时长页面", httpMethod = "GET", notes = "设置分享时长页面")
    public ActResult setShareTime(HttpServletRequest request,@ApiParam(required =true, name = "课程ID", value = "课程ID")  Long id,@ApiParam(required =true, name = "时长", value = "时长")  Long mustShareTime) throws Exception {
        ActResult result = new ActResult();
        try {
            Course course = courseService.getCourse(id);
            Long recTime = 0l;
            if (!Utility.isNullorEmpty(course)) {
                recTime = course.getRecTime();
            }
            if (mustShareTime == 0) {
                mustShareTime = recTime;
            }
        /*    if(mustShareTime>recTime){
                result.setCode(1001);
                result.setMsg("分享时长不能大于录播时长！");
                return result;
            }*/
            courseService.setShareTime(mustShareTime, id);
            String courseKey = RedisKey.ll_course + id;
            redisUtil.del(courseKey);
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("设置分享时长失败");
        }
        return result;
    }
}
