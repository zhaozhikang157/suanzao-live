package com.longlian.live.controller;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.UserAgentUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.dto.UserAgent;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.SpringContextUtil;
import com.huaxin.util.weixin.type.WechatQRCodeType;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.*;
import com.longlian.live.task.CreateRelayCourse;
import com.longlian.live.util.ShortMessage;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.live.util.SystemUtil;
import com.longlian.live.util.guava.F;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.AppUser;
import com.longlian.model.Course;
import com.longlian.model.LiveRoom;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.MsgType;
import com.longlian.type.ReturnMessageType;
import com.longlian.type.SMSTemplate;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by pangchao on 2017/2/12.
 */
@Controller
@RequestMapping(value = "/course")
public class CourseController {
    private static Logger log = LoggerFactory.getLogger(CourseController.class);
    @Value("${website}")
    private String website;
    @Autowired
    CourseService courseService;
    @Autowired
    UpdateAndCreateCourseService updateAndCreateCourseService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;
    @Autowired
    MsgCancelService msgCancelService;
    @Autowired
    WeixinUtil weixinUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    MobileVersionService versionService;
    @Autowired
    AppMsgService appMsgService;
    @Autowired
    EndLiveService endLiveService;
    @Autowired
    ShortMessage shortMessage;
    @Autowired
    CourseRelayService courseRelayService;
    @Autowired
    SendMsgService sendMsgService;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    AppUserService appUserService;
    @Autowired
    WechatOfficialService wechatOfficialService;

    /**
     * 创建单节课
     **/
    @RequestMapping(value = "/createCourse.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "创建单节课", httpMethod = "POST", notes = "创建单节课")
    public ActResultDto createCourse(HttpServletRequest request, CourseDto course, String uuid) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        //解决Android重复提交问题存在uuid
        if (!StringUtils.isEmpty(uuid) && redisUtil.exists(RedisKey.create_course_request_uuid + uuid)) {
            return actResultDto;
        }
        redisUtil.setex(RedisKey.create_course_request_uuid + uuid , 10 , "1");

        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        course.setAppId(token.getId());
        UserAgent userAgent = UserAgentUtil.getUserAgentCustomer(request);
        //解决苹果重复提交问题 苹果加强重复提交判断（根据提交人判断）
        if (userAgent!=null &&  UserAgentUtil.ios.equals(userAgent.getCustomerType())) {
            if (redisUtil.exists(RedisKey.create_course_request_uuid + token.getId())) {
                return actResultDto;
            }
            redisUtil.setex(RedisKey.create_course_request_uuid + token.getId() , 5 , "1");
        }

        LiveRoom liveRoom = liveRoomService.findByAppId(token.getId());
        if(liveRoom!=null){
            course.setAutoCloseTime(liveRoom.getAutoCloseTime());
        }
        if (course.getRoomId() == 0) {
            course.setRoomId(liveRoom.getId());
        }

        //解决苹果以前的传参错误（兼容）
        if ("5:5".equals(course.getDivideScale())) {
            course.setDivideScale("0");
        }

        if ("6:4".equals(course.getDivideScale())) {
            course.setDivideScale("1");
        }

        if ("10000".equals(course.getDivideScale())) {
            course.setDivideScale("0");
        }
        if (!Utility.isNullorEmpty(course.getLiveTopic())) {
            course.setLiveTopic(SystemUtil.processQuotationMarks(course.getLiveTopic()));
        }
        //如果直播间处理禁用状态，不让建课
        if ("1".equals(liveRoom.getRoomStatus())) {
            actResultDto.setCode(ReturnMessageType.ROOM_FORBIDDEN_NOT_CREATE.getCode());
            actResultDto.setMessage(ReturnMessageType.ROOM_FORBIDDEN_NOT_CREATE.getMessage());
            redisUtil.del(RedisKey.create_course_request_uuid + token.getId());
            return actResultDto;
        }
        //在创建课程前会判断现在是否有课正在直播，如果有则先需要停止正在直播的课程并下架
        //isCloseId就是客户端人员选了：需要先结束课程的选项
        String isCloseId = request.getParameter("isCloseId");
        if (!Utility.isNullorEmpty(isCloseId) && Integer.valueOf(isCloseId) == 1) {
            //正在直播的课程
            List<Course> courseList = courseService.getLivingForCourse(token.getId());
            if (courseList.size() > 0) {
                //逐个结束并下架课程
                for (Course cloCourse : courseList) {
                    if ("1".equals(cloCourse.getIsSeriesCourse())) {
                        continue;
                    }
                    endLiveService.endLive(cloCourse);
                    //courseService.setCourseDown(cloCourse);

                }
            }
        }
        //创建课程核心逻辑
        actResultDto = updateAndCreateCourseService.createCourse(course);
        //苹果加强重复提交判断（根据提交人判断）， 如果是里面报错了，则应该删除这个标志
        if (!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode())) {
            redisUtil.del(RedisKey.create_course_request_uuid + token.getId());
        }
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
     * 课程列表
     **/
    @RequestMapping(value = "/getCourseList", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "课程列表", httpMethod = "GET", notes = "课程列表")
    public ActResultDto getCourseList(HttpServletRequest request,
                                      @ApiParam(required = true, name = "直播间ID", value = "直播间ID") Long roomId,
                                      @ApiParam(required = false, name = "版本", value = "版本号") String v,
                                      @ApiParam(required = false, name = "直播状态", value = "直播状态") String status,
                                      @ApiParam(required = true, name = "当前页码", value = "当前页码") Integer pageNum,
                                      @ApiParam(required = true, name = "没有条数", value = "每页条数") Integer pageSize) throws Exception {
        ActResultDto result = new ActResultDto();
        if (roomId == null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
            List list = courseService.getCourseList(roomId, pageNum, pageSize, isHaveRecord, status, v);
            if (list != null && list.size() > 0) {
                result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                result.setData(list);
            } else {
                result.setCode(ReturnMessageType.NO_DATA.getCode());
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            }
        }
        return result;
    }


    /**
     * 获取分销比例集合
     **/
    @RequestMapping(value = "/getCourseDivideScale", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取分销比例集合", httpMethod = "GET", notes = "获取分销比例集合")
    public ActResultDto getCourseDivideScale() throws Exception {
        ActResultDto ac = new ActResultDto();
        List list = systemParaRedisUtil.getCourseDivideScale();
        if (list.size() > 0) {
            ac.setData(list);
        } else {
            ac.setMessage("暂无分销比例");
        }
        return ac;
    }

    /**
     * 课程统计详情
     **/
    @RequestMapping(value = "/getCourseDetails.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "课程统计详情", httpMethod = "GET", notes = "课程统计详情")
    public ActResultDto getCourseDetails(@ApiParam(required = true, name = "课程ID", value = "课程ID") Long id) throws Exception {
        ActResultDto ac = new ActResultDto();
        if (id == null) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            Map map = courseService.getCourseDetails(id);
            if(map == null){
                ac.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                ac.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return ac;
            }
            ac.setData(courseService.getCourseDetails(id));
        }
        return ac;
    }

    /**
     * 课程详情 预告、直播
     **/
    @RequestMapping(value = "/getCourseInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "课程预告,直播", httpMethod = "GET", notes = "课程预告,直播")
    public ActResultDto getCourseInfo(HttpServletRequest request, long id) throws Exception {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        String v = request.getParameter("v");
        String type = request.getParameter("type");
        long appid = 0;
        String mobile = "";
        if (token != null) {
            appid = token.getId();
            mobile = token.getMobile();
        }
        ActResultDto ac = new ActResultDto();
        long shortId=0;
        //如果id是UUID，则查询转播表
        if(id!=0 && String.valueOf(id).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            //转播课
            ac = courseService.getRelayCourseInfo(id, appid, UserAgentUtil.isWechatClient(request), v);
        }else {
            shortId=id;
            ac = courseService.getCourseInfo(shortId, appid, UserAgentUtil.isWechatClient(request), v,type);
        }

        String msgCancels = "";
//            try {
//                msgCancels = msgCancelService.findMsgCancel(id);
//            }catch (Exception e){
//                msgCancels = "";
//            }
        if(ac.getData() != null){
            Map map = (Map) ac.getData();
            map.put("msgCancelId", msgCancels);
        }


        ac.setExt(mobile);//存放手机，为微信使用
        return ac;
    }

    /**
     * 课程详情 预告、直播
     **/
    @RequestMapping(value = "/getCourseInfo.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "课程预告,直播", httpMethod = "GET", notes = "课程预告,直播")
    public ActResultDto getCourseInfoUser(HttpServletRequest request, long id) throws Exception {
        return getCourseInfo(request, id);
    }

    /**
     * 搜索相关课程
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/findCoursebyNamePage")
    @ResponseBody
    @ApiOperation(value = "搜索相关课程", httpMethod = "GET", notes = "搜索相关课程")
    public ActResultDto findCoursebyName(String name, Integer offset, Integer pageSize) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (offset == null) {
            offset = 0;
        }
        return courseService.findCoursebyName(name, offset, pageSize);
    }

    /**
     * 搜索相关课程
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/findCoursebyNamePageV2")
    @ResponseBody
    @ApiOperation(value = "搜索相关课程", httpMethod = "GET", notes = "搜索相关课程")
    public ActResultDto findCoursebyNamePageV2(HttpServletRequest request, String name, Integer offset, Integer pageSize) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (offset == null) {
            offset = 0;
        }
        boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
        return courseService.findCoursebyNameV2(name, offset, pageSize, isHaveRecord);
    }

    /**
     * 搜索相关课程
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/findCoursebyNamePageV2.user")
    @ResponseBody
    @ApiOperation(value = "搜索相关课程", httpMethod = "GET", notes = "搜索相关课程")
    public ActResultDto findCoursebyNamePageV2User(HttpServletRequest request, String name, Integer offset, Integer pageSize) {
        return findCoursebyNamePageV2(request, name, offset, pageSize);
    }

    /**
     * 录音时长
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/getRecTimeByCourseId")
    @ResponseBody
    @ApiOperation(value = "获取录音时长", httpMethod = "GET", notes = "获取录音时长")
    public ActResultDto getRecTimeByCourseId(@ApiParam(required = true, name = "课程ID", value = "课程ID") Long courseId) {
        return courseService.getRecTimeByCourseId(courseId);
    }

    /**
     * 录音时长设置
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/setRecoTimeById")
    @ResponseBody
    @ApiOperation(value = "录音时长设置", httpMethod = "GET", notes = "录音时长设置")
    public ActResultDto setRecoTimeById(@ApiParam(required = true, name = "课程ID", value = "课程ID") Long courseId,
                                        @ApiParam(required = true, name = "录音时长", value = "录音时长") Integer recoTime) {
        return courseService.setRecoTimeById(courseId, recoTime);
    }

    /**
     * 录音时长设置
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/setRecoTimeById.user")
    @ResponseBody
    @ApiOperation(value = "录音时长设置", httpMethod = "GET", notes = "录音时长设置")
    public ActResultDto setRecoTimeByIdUser(@ApiParam(required = true, name = "课程ID", value = "课程ID") Long courseId,
                                            @ApiParam(required = true, name = "录音时长", value = "录音时长") Integer recoTime) {
        return setRecoTimeById(courseId, recoTime);
    }

    /**
     * 判断课程的加密码
     **/
    @RequestMapping(value = "/getLivePwd", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "判断课程的加密码", httpMethod = "GET", notes = "判断课程的加密码")
    public ActResultDto getLivePwd(@ApiParam(required = true, name = "课程ID", value = "课程ID") Long id,
                                   @ApiParam(required = true, name = "课程加密码", value = "课程加密码") String livePwd) {
        ActResultDto ac = new ActResultDto();
        if (id == null || livePwd == null) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            if (!courseService.getLivePwd(id, livePwd)) {
                ac.setCode(ReturnMessageType.ERROR_LIVE_PWD.getCode());
                ac.setMessage(ReturnMessageType.ERROR_LIVE_PWD.getMessage());
            }
        }
        return ac;
    }

    /**
     * 单节课的修改
     **/
    @RequestMapping(value = "/uploadCourse.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "单节课的修改", httpMethod = "POST", notes = "单节课的修改")
    public ActResultDto uploadCourse(HttpServletRequest request,@ApiParam(required =true, name = "单节课对象", value = "单节课对象") CourseDto course) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto ac = new ActResultDto();
        try {
            if (course.getId() == 0) {
                ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
                ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
                return ac;
            }
            if(course.getRelayScale()>100F || Float.valueOf(course.getRelayScale())<0F){
                ac.setCode(ReturnMessageType.CHECK_RELAY_SCALE.getCode());
                ac.setMessage(ReturnMessageType.CHECK_RELAY_SCALE.getMessage());
                return ac;
            }
            if (!Utility.isNullorEmpty(course.getLiveTopic())) {
                course.setLiveTopic(SystemUtil.processQuotationMarks(course.getLiveTopic()));
            }
            Course c = courseService.getCourse(course.getId());
            List<Course> courseLivingList = courseService.getshortTimeCourse(c.getRoomId());
            if (courseLivingList.size() > 0) {
                for (Course liveCourse : courseLivingList) {
                    if (Utility.isNullorEmpty(liveCourse.getStartTime()) || liveCourse.getId() == course.getId()) {
                        continue;
                    }
                    Date beforeTime = DateUtil.parseDate("yyyy-MM-dd hh:mm:ss", DateUtil.getTimeByMinute(liveCourse.getStartTime(), -11));
                    Date afterTime = DateUtil.parseDate("yyyy-MM-dd hh:mm:ss", DateUtil.getTimeByMinute(liveCourse.getStartTime(), 11));
                    if (beforeTime.getTime() < course.getStartTime().getTime() && course.getStartTime().getTime() < afterTime.getTime()) {
                        ac.setCode(ReturnMessageType.EDIT_COURSE_TIME_SHORT.getCode());
                        ac.setMessage(ReturnMessageType.EDIT_COURSE_TIME_SHORT.getMessage());
                        return ac;
                    }
                }
            }
            if (c == null || token.getId() != c.getAppId()) {////判断直播间是否是当前用户的
                ac.setCode(ReturnMessageType.COURSE_NOT_MODIFY.getCode());
                ac.setMessage(ReturnMessageType.COURSE_NOT_MODIFY.getMessage());
            } else {

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

                updateAndCreateCourseService.updateCourse(course, request);
                if(c.getIsRelay()!=1 && course.getIsRelay()==1){
                    //发送公众号消息
                    CourseDto courseDto=new CourseDto();
                    courseDto.setLiveTopic(c.getLiveTopic());
                    courseDto.setId(c.getId());
                    courseDto.setIsRelay(1);
                    courseDto.setRoomId(c.getRoomId());
                    courseDto.setRemark(c.getRemark());
                    courseDto.setCoverssAddress(c.getCoverssAddress());
                    courseDto.setLiveTopic(c.getLiveTopic());
                    courseDto.setStartTime(c.getStartTime());
                    AppUser teach = appUserService.getById(c.getAppId());
                    courseDto.setAppUserName(teach.getName());
                    wechatOfficialService.getSendWechatTemplateMessageSaveRedis(courseDto);

                    String cAct = website + "/weixin/relaymarket.user";
                    //老师消息
                    sendMsgService.sendMsg(course.getAppId(), MsgType.SYS_OTHER_RELAY.getType(), course.getId() ,
                            MsgConst.replace(MsgConst.RELAY_COUSER_REMIND_FOLLOW_USER_CONTENT,course.getLiveTopic()) , cAct);
                    //粉丝消息
                    Set<String> follows=userFollowService.getFollowUser(course.getRoomId());
                    AppUser user = appUserService.getById(token.getId());
                    //cAct = website + "/weixin/liveRoom?id="+token.getId();
                    for(String userId:follows){
                        sendMsgService.sendMsg(Long.parseLong(userId), MsgType.SYS_OTHER_RELAY.getType(), course.getId()
                                , MsgConst.replace(MsgConst.RELAY_COURSE_SUCCESS, user.getName())
                                , cAct);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ac.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            ac.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        return ac;
    }


    /*
     * 获取课程轮播图
     * @param id
     * @return
     */
    @RequestMapping(value = "/getCourseWareById")
    @ResponseBody
    @ApiOperation(value = "获取课程轮播图", httpMethod = "GET", notes = "获取课程轮播图")
    public ActResultDto getCourseWareById(@ApiParam(required = true, name = "课程id", value = "课程id") Long id) {
        return courseService.getCourseWareById(id);
    }

    /*
     * 获取课程轮播图
     * @param id
     * @return
     */
    @RequestMapping(value = "/getCourseWareById.user")
    @ResponseBody
    @ApiOperation(value = "获取课程轮播图", httpMethod = "GET", notes = "获取课程轮播图")
    public ActResultDto getCourseWareByIdUser(@ApiParam(required = true, name = "课程id", value = "课程id") Long id) {
        return getCourseWareById(id);
    }


    /**
     * 获取10条课程数据
     **/
    @RequestMapping(value = "/getCourseListByLiveRoom", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取10条课程数据", httpMethod = "GET", notes = "获取10条课程数据")
    public ActResultDto getCourseListByLiveRoom(@ApiParam(required = true, name = "直播间ID", value = "直播间ID") Long id) throws Exception {
        ActResultDto result = new ActResultDto();
        if (id == null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            List<CourseDto> list = courseService.getCourseListByLiveRoom(id);
            result.setData(list);
        }
        return result;
    }

    /**
     * 老师的设置空间
     **/
    @RequestMapping(value = "/getTeacherSetByCourseId.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "老师的设置空间", httpMethod = "GET", notes = "老师的设置空间")
    public ActResultDto getTeacherSetByCourseId(HttpServletRequest request,
                                                @ApiParam(required = true, name = "课程ID", value = "课程ID") Long courseId) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (courseId == null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            Map map = courseService.getTeacherSetByCourseId(courseId, token.getId());
            result.setData(map);
        }
        return result;
    }

    /**
     * 课程详情 , 直播 手机端接口
     **/
    @RequestMapping(value = "/findCourseInfoById", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "课程详情", httpMethod = "GET", notes = "课程详情")
    public ActResultDto findCourseInfoById(HttpServletRequest request,
                                           @ApiParam(required = true, name = "课程ID", value = "课程ID") Long courseId) throws Exception {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        long appid = 0;
        if (token != null) {
            appid = token.getId();
        }

        return courseService.findCourseInfoById(courseId, appid);
    }

    /**
     * 课程详情 , 直播 手机端接口
     **/
    @RequestMapping(value = "/findCourseInfoById.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "课程详情", httpMethod = "GET", notes = "课程详情")
    public ActResultDto findCourseInfoByIdUser(HttpServletRequest request,
                                               @ApiParam(required = true, name = "课程ID", value = "课程ID") Long courseId) throws Exception {
        return findCourseInfoById(request, courseId);
    }


    /**
     * 首页中点击更多的磕碜
     **/
    @RequestMapping(value = "/getMoreLiveCourse", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "首页中点击更多", httpMethod = "GET", notes = "首页中点击更多")
    public ActResultDto getMoreLiveCourse(@ApiParam(required = true, name = "直播状态", value = "直播状态") Long liveStatus,
                                          Long courseTypeId, Integer pageNum, Integer pageSize) throws Exception {
        ActResultDto ac = new ActResultDto();
        if (liveStatus == null) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            List<Map> list = courseService.getMoreLiveCourse(liveStatus, courseTypeId, pageNum, pageSize);
            if (list != null && list.size() > 0) {
                ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                ac.setData(list);
            } else {
                ac.setCode(ReturnMessageType.NO_DATA.getCode());
                ac.setMessage(ReturnMessageType.NO_DATA.getMessage());
            }
        }
        return ac;
    }

    /**
     * 课程是否免费或报名
     **/
    @RequestMapping(value = "/getCourseIsFreeSign", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "课程是否免费或报名", httpMethod = "GET", notes = "课程是否免费或报名")
    public ActResultDto getCourseIsFreeSign(@ApiParam(required = true, name = "课程ID", value = "课程ID") Long courseId) throws Exception {
        ActResultDto ac = new ActResultDto();
        if (courseId == null) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            Map map = courseService.getCourseIsFreeSign(courseId);
            ac.setData(map);
        }
        return ac;
    }

    /**
     * 课程下线
     **/
    @RequestMapping(value = "/setCourseDown.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "课程下线", httpMethod = "GET", notes = "课程下线")
    public ActResultDto setCourseDown(HttpServletRequest request,  Long id) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto ac = new ActResultDto();
        if (id == null || id == 0) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            try {
                ac = courseService.setCourseDown(id, token.getId());
            } catch (Exception e) {
                log.error("课程下线失败", e);
                ac.setCode(ReturnMessageType.COURSE_DOWN_ERROR.getCode());
                ac.setMessage(ReturnMessageType.COURSE_DOWN_ERROR.getMessage());
            }
        }
        return ac;
    }

    /**
     * 获取课程二维码
     *
     * @param roomId
     * @param courseId
     * @param request
     * @return
     */
    @RequestMapping("/getQrAddress.user")
    @ResponseBody
    @ApiOperation(value = "获取课程二维码", httpMethod = "GET", notes = "获取课程二维码")
    public ActResultDto getQrAddress(@ApiParam(required = true, name = "直播间ID", value = "直播间ID") Long roomId,
                                     @ApiParam(required = true, name = "课程ID", value = "课程ID") Long courseId, HttpServletRequest request) {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        ActResultDto resultDto = new ActResultDto();
        if (roomId == null || courseId == null) {
            resultDto.setCode(ReturnMessageType.GET_RQCODE_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.GET_RQCODE_ERROR.getMessage());
            return resultDto;
        }
        if (roomId < 1 || courseId < 1) {
            resultDto.setCode(ReturnMessageType.GET_RQCODE_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.GET_RQCODE_ERROR.getMessage());
            return resultDto;
        }
        String address = weixinUtil.getParaQrcode(WechatQRCodeType.third_wechat_or_course_param.getValue(), roomId, courseId, token.getId(), 0);
        if (StringUtils.isEmpty(address)) {
            resultDto.setCode(ReturnMessageType.GET_RQCODE_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.GET_RQCODE_ERROR.getMessage());
            return resultDto;
        }
        resultDto.setData(address);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    /**
     * 根据直播间ID查询该直播间的课程
     *
     * @param roomId
     * @param noLearnOffset      //未开播偏移量
     * @param pageSize
     * @param alreadyLearnOffset //已开播偏移量
     * @return
     */
    @RequestMapping(value = "/findCourseByRoomId")
    @ResponseBody
    @ApiOperation(value = "根据直播间ID查询该直播间的课程", httpMethod = "GET", notes = "根据直播间ID查询该直播间的课程")
    public ActResultDto findCourseByRoomId(HttpServletRequest request,
                                           @ApiParam(required = true, name = "直播间ID", value = "直播间ID") Long roomId,
                                           Integer noLearnOffset, Integer pageSize, Integer alreadyLearnOffset) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (alreadyLearnOffset == null) {
            alreadyLearnOffset = 0;
        }
        if (noLearnOffset == null) {
            noLearnOffset = 0;
        }
        boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
        return courseService.findCourseByRoomId(noLearnOffset, pageSize, roomId, alreadyLearnOffset, isHaveRecord);
    }

    /**
     * 根据直播间ID查询该直播间的课程
     *
     * @param roomId
     * @param noLearnOffset      //未开播偏移量
     * @param pageSize
     * @param alreadyLearnOffset //已开播偏移量
     * @return
     */
    @RequestMapping(value = "/findCourseByRoomId.user")
    @ResponseBody
    @ApiOperation(value = "根据直播间ID查询该直播间的课程", httpMethod = "GET", notes = "根据直播间ID查询该直播间的课程")
    public ActResultDto findCourseByRoomIdUser(HttpServletRequest request,
                                               @ApiParam(required = true, name = "直播间ID", value = "直播间ID") Long roomId,
                                               Integer noLearnOffset, Integer pageSize, Integer alreadyLearnOffset) {
        return findCourseByRoomId(request, roomId, noLearnOffset, pageSize, alreadyLearnOffset);
    }

    /**
     * 下架系列课
     *
     * @param courseId
     * @return
     */
    @RequestMapping("/closeSeries")
    @ResponseBody
    @ApiOperation(value = "下架系列课", httpMethod = "GET", notes = "下架系列课")
    public ActResultDto closeSeries(HttpServletRequest request,
                                    @ApiParam(required = true, name = "课程ID", value = "课程ID") Long courseId) {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if (token == null) {
            ActResultDto resultDto = new ActResultDto();
            resultDto.setCode(ReturnMessageType.ERROR_403.getCode());
            resultDto.setMessage(ReturnMessageType.ERROR_403.getMessage());
            return resultDto;
        }
        ActResultDto resultDto = courseService.closeSeries(courseId, token.getId());
        Course course = courseBaseService.getCourseFromRedis(courseId);
        if (course != null) {
            appMsgService.updateMsgCourseStatus(courseId, "1" , course.getRoomId());
        }
        String courseKey = RedisKey.ll_course + courseId;
        redisUtil.del(courseKey);
        return resultDto;
    }

    /**
     * 下架系列课
     *
     * @param courseId
     * @return
     */
    @RequestMapping("/closeSeries.user")
    @ResponseBody
    @ApiOperation(value = "下架系列课", httpMethod = "GET", notes = "下架系列课")
    public ActResultDto closeSeriesUser(HttpServletRequest request,
                                        @ApiParam(required = true, name = "课程ID", value = "课程ID") Long courseId) {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if (token == null) {
            ActResultDto resultDto = new ActResultDto();
            resultDto.setCode(ReturnMessageType.ERROR_403.getCode());
            resultDto.setMessage(ReturnMessageType.ERROR_403.getMessage());
            return resultDto;
        }
        ActResultDto resultDto = closeSeries(request, courseId);
        String courseKey = RedisKey.ll_course + courseId;
        redisUtil.del(courseKey);
        return resultDto;
    }

    /**
     * 设置课件页码
     */
    @RequestMapping("/setCourseIndex.user")
    @ResponseBody
    @ApiOperation(value = "设置课件页码", httpMethod = "GET", notes = "设置课件页码")
    public ActResultDto setCourseIndex(Long courseId, HttpServletRequest request,
                                       @ApiParam(required = true, name = "页码", value = "页码") Long index) {
        ActResultDto resultDto = new ActResultDto();
        if (courseId == null || courseId < 1) {
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        if (index == null) {
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        redisUtil.set(RedisKey.ll_course_class_index + courseId, String.valueOf(index));
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    /**
     * 清屏
     *
     * @return
     */
    @RequestMapping(value = "/clearScreen.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "清屏", httpMethod = "GET", notes = "清屏")
    public ActResultDto clearScreen( Long courseId ,
                                    HttpServletRequest request) {
        ActResultDto resultDto = new ActResultDto();
        if (courseId == null || courseId < 1) {
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        resultDto = courseService.clearScreenByChatRoomId(courseId, token.getId());
        return resultDto;
    }

    /**
     * 关闭正在直播未结束的课
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/isSameCoureForLiving.user")
    @ResponseBody
    @ApiOperation(value = "关闭正在直播未结束的课", httpMethod = "GET", notes = "关闭正在直播未结束的课")
    public ActResultDto isSameCoureForLiveing(HttpServletRequest request) {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        ActResultDto resultDto = new ActResultDto();
        resultDto = courseService.isSameCoureForLiving(token.getId());
        return resultDto;
    }

    /**
     * 修改连麦接口
     *
     * @param request
     * @param courseId
     * @param isCan    0-不允许 1-允许
     * @return
     */
    @RequestMapping("/setCanConnect.user")
    @ResponseBody
    @ApiOperation(value = "设置是否连麦", httpMethod = "GET", notes = "设置是否连麦")
    public ActResultDto setcanConnect(HttpServletRequest request,
                                      Long courseId,
                                       String isCan) {
        ActResultDto resultDto = new ActResultDto();
        if (courseId == null || courseId < 1) {
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        Course course = courseService.getCourseFromRedis(courseId);
        if (course == null) {
            resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return resultDto;
        }
        if (token.getId() != course.getAppId()) {
            resultDto.setCode(ReturnMessageType.ONLY_TEACHER_OPTION.getCode());
            resultDto.setMessage(ReturnMessageType.ONLY_TEACHER_OPTION.getMessage());
            return resultDto;
        }
        if (StringUtils.isEmpty(isCan)) {
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        return courseService.updateCanConnect(courseId, isCan);
    }

    /**
     * 我的课程 -- 观看历史
     *
     * @param historyOffset
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "getMyHistoryCourseList.user")
    @ResponseBody
    @ApiOperation(value = " 我的课程 -- 观看历史", httpMethod = "GET", notes = " 我的课程 -- 观看历史")
    public ActResultDto getMyHistoryCourseList(Integer historyOffset, HttpServletRequest request,String clientType) throws Exception {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if (historyOffset == null) {
            historyOffset = 0;
        }
        DataGridPage page = new DataGridPage();
        page.setOffset(historyOffset);
        ActResultDto  result =courseService.getMyHistoryCourseList(page, token.getId(),clientType);
        return result;
    }

    /**
     * 我的课程 -- 购买课程
     *
     * @param buyOffset
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "getMyBuyFreeCourseList.user")
    @ResponseBody
    @ApiOperation(value = " 我的课程 -- 购买课程", httpMethod = "GET", notes = " 我的课程 -- 购买课程")
    public ActResultDto getMyBuyFreeCourseList(Integer buyOffset, HttpServletRequest request,String clientType) throws Exception {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if (buyOffset == null) {
            buyOffset = 0;
        }
        DataGridPage page = new DataGridPage();
        page.setOffset(buyOffset);
        ActResultDto result = courseService.getMyBuyFreeCourseList(page, token.getId(),clientType);
        return result;

    }

    /**
     * 我的课程 -- 未播/直播 课程
     *
     * @param liveOffset
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "getMyLiveCourseList.user")
    @ResponseBody
    @ApiOperation(value = " 我的课程 --未播/直播 课程", httpMethod = "GET", notes = " 我的课程 -- 未播/直播 课程")
    public ActResultDto getMyLiveCourseList(Integer liveOffset, HttpServletRequest request,String clientType) throws Exception {
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        DataGridPage dg = new DataGridPage();
        if (liveOffset == null) {
            liveOffset = 0;
        }
        dg.setOffset(liveOffset);
        ActResultDto result = courseService.getMyLivedCourseList(dg, token.getId(),clientType);
        return result;
    }

    /**
     * 免费专区
     *
     * @param offset
     * @param pageSize
     * @param sc
     * @param sort
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "findFreeAdmissionCoursePage")
    @ResponseBody
    @ApiOperation(value = " 免费专区", httpMethod = "GET", notes = " 免费专区")
    public ActResultDto findFreeAdmissionCoursePage(Integer offset, Integer pageSize, String sc, String sort) throws Exception {
        return courseService.findFreeAdmissionCoursePage(offset, pageSize, sort, sc);
    }


    /**
     * 每周精选更多
     *
     * @param offset
     * @param pageSize
     * @param timeStr  0-本周 1-上周 3-其他
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findWeeklySelectionPage")
    @ResponseBody
    @ApiOperation(value = " 每周精选更多", httpMethod = "GET", notes = " 每周精选更多")
    public ActResultDto findWeeklySelectionPage(HttpServletRequest request,Integer offset, Integer pageSize,
                                                @ApiParam(required = true, name = "时间", value = " 0-本周 1-上周 3-其他") String timeStr) throws Exception {
        ActResultDto weeklySelectionPage = courseService.findWeeklySelectionPage(offset, pageSize, timeStr);

        return weeklySelectionPage;
    }

    /**
     * 新版首页--正在直播
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findBeingroadcastive")
    @ResponseBody
    @ApiOperation(value = " 新版首页--正在直播", httpMethod = "GET", notes = " 新版首页--正在直播")
    public ActResultDto findBeingroadcastive(Integer offset, Integer pageSize) throws Exception {
        return courseService.findBeingroadcastive(offset, pageSize);
    }

    /**
     * 新版首页--未开播
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findCommend4HomeV4Page")
    @ResponseBody
    @ApiOperation(value = " 新版首页--未开播", httpMethod = "GET", notes = " 新版首页--未开播")
    public ActResultDto findCommend4HomeV4Page(Integer offset, Integer pageSize) throws Exception {
        return courseService.findCommend4HomeV4Page(offset, pageSize);
    }

    /**
     * 首页接口-排行榜
     *
     * @param offset   偏移量
     * @param pageSize 每页数量
     * @param sc       时间正序--0 时间倒序--1
     * @param sort     综合排序--0 人气排序--1 时间排序--2 3--价格
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findRankingListPage")
    @ResponseBody
    @ApiOperation(value = " 首页接口-排行榜", httpMethod = "GET", notes = " 首页接口-排行榜")
    public ActResultDto findRankingListPage(Integer offset, Integer pageSize,
                                            @ApiParam(required = true, name = "时间排序", value = "正序--0 , 倒序--1") String sc,
                                            @ApiParam(required = true, name = "综合排序", value = "人气--1,时间--2,价格--3") String sort) throws Exception {
        return courseService.findRankingListPage(offset, pageSize, sort, sc);
    }


    @RequestMapping(value = "/xxxxxxxx")
    @ResponseBody
    public void xxxxxxxx(Optional<String> ss) throws Exception {
        shortMessage.send_message2(Optional.of(SMSTemplate.SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD.getName()),
                Optional.of(SMSTemplate.SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD.getCode()), ss,"88888");
    }

    /**
     * 创建单节课 加入自定义分销比例
     **/
    @RequestMapping(value = "/createCourseNew.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "创建单节课 加入自定义分销比例", httpMethod = "POST", notes = "创建单节课")
    public ActResultDto createCourseNew(HttpServletRequest request, CourseDto course, String uuid) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        //开课时间不能晚于当前时间前5分钟
        Date createTime = course.getStartTime();
        Calendar noww = Calendar.getInstance();
        noww.add(Calendar.MINUTE, -5);
        if(createTime.before(noww.getTime())){
            actResultDto.setCode(ReturnMessageType.COURSE_START_TIME_BEFORE_NOW.getCode());
            actResultDto.setMessage(ReturnMessageType.COURSE_START_TIME_BEFORE_NOW.getMessage());
            return actResultDto;
        }
        //解决Android重复提交问题存在uuid
        if (!StringUtils.isEmpty(uuid) && redisUtil.exists(RedisKey.create_course_request_uuid + uuid)) {
            return actResultDto;
        }
        redisUtil.setex(RedisKey.create_course_request_uuid + uuid , 10 , "1");

        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        course.setAppId(token.getId());
        UserAgent userAgent = UserAgentUtil.getUserAgentCustomer(request);
        //解决苹果重复提交问题 苹果加强重复提交判断（根据提交人判断）
        if (userAgent!=null &&  UserAgentUtil.ios.equals(userAgent.getCustomerType())) {
            if (redisUtil.exists(RedisKey.create_course_request_uuid + token.getId())) {
                return actResultDto;
            }
            redisUtil.setex(RedisKey.create_course_request_uuid + token.getId() , 5 , "1");
        }

        LiveRoom liveRoom = liveRoomService.findByAppId(token.getId());//根据用户ID 查出直播间
        if(liveRoom!=null){//把直播间的自动结束时间加入到 直播间下新建课程的自动结束时间里面
            course.setAutoCloseTime(liveRoom.getAutoCloseTime());
        }
        if (course.getRoomId() == 0) {//当用户未设置课程所在直播间时 此处设置
            course.setRoomId(liveRoom.getId());
        }
        if (!Utility.isNullorEmpty(course.getLiveTopic())) {
            course.setLiveTopic(SystemUtil.processQuotationMarks(course.getLiveTopic()));
        }
        //如果直播间处理禁用状态，不让建课
        if ("1".equals(liveRoom.getRoomStatus())) {
            actResultDto.setCode(ReturnMessageType.ROOM_FORBIDDEN_NOT_CREATE.getCode());
            actResultDto.setMessage(ReturnMessageType.ROOM_FORBIDDEN_NOT_CREATE.getMessage());
            redisUtil.del(RedisKey.create_course_request_uuid + token.getId());
            return actResultDto;
        }
        //在创建课程前会判断现在是否有课正在直播，如果有则先需要停止正在直播的课程并下架
        //isCloseId就是客户端人员选了：需要先结束课程的选项
        String isCloseId = request.getParameter("isCloseId");
        if (!Utility.isNullorEmpty(isCloseId) && Integer.valueOf(isCloseId) == 1) {
            //正在直播的课程
            List<Course> courseList = courseService.getLivingForCourse(token.getId());
            if (courseList.size() > 0) {
                //逐个结束并下架课程
                for (Course cloCourse : courseList) {
                    if ("1".equals(cloCourse.getIsSeriesCourse())) {
                        continue;
                    }
                    endLiveService.endLive(cloCourse);
//                    courseService.setCourseDown(cloCourse);

                }
            }
        }
        //创建课程核心逻辑
        actResultDto = updateAndCreateCourseService.createCourse(course);
        //开启另一个线程创建转播课程（系列单节课）
        if(course.getSeriesCourseId() > 0){
            CreateRelayCourse createRelayCourse = SpringContextUtil.getBeanByType(CreateRelayCourse.class);
            createRelayCourse.setAppId(token.getId());
            createRelayCourse.setCourseId(course.getId());
            createRelayCourse.setRoomId(course.getRoomId());
            F.submit(createRelayCourse);
        }
        //苹果加强重复提交判断（根据提交人判断）， 如果是里面报错了，则应该删除这个标志
        if (!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode())) {
            redisUtil.del(RedisKey.create_course_request_uuid + token.getId());
        }
        return actResultDto;
    }

    /**
     * 分类
     *
     * @param offset
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getCoursesByType")
    @ResponseBody
    @ApiOperation(value = "分类", httpMethod = "GET", notes = "分类")
    public ActResultDto getCoursesByType(Integer offset, Integer pageSize,String courseType) throws Exception {
        return courseService.getCoursesByType(offset, pageSize, courseType);
    }

    /**
     * 获取课程分销收益金额
     * @param request
     * @param id
     * @return
     */
    @RequestMapping("/getCourseCardAmt.user")
    @ResponseBody
    @ApiOperation(value = "获取课程分销收益金额以及比例", httpMethod = "GET", notes = "获取课程分销收益金额以及比例")
    public ActResultDto getCourseCardAmt(HttpServletRequest request,
                                         @ApiParam(required = true, name = "课程id", value = "课程id") long id){
        ActResultDto dto = new ActResultDto();
        if(id <= 0){
            dto.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
            dto.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            return dto;
        }
        AppUserIdentity identity = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if(identity != null){
            dto = courseService.getCourseCardAmt(id,identity.getId());
        }else{
            dto.setCode(ReturnMessageType.ERROR_403.getCode());
            dto.setMessage(ReturnMessageType.ERROR_403.getMessage());
        }
        return dto;
    }

    /**
     * 修改课程转播信息
     * @param courseId   课程id
     * @param isRelay   是否允许转播 1：是  0：否
     * @param relayCharge  转播价格
     * @param relayScale  转播课购买收益比例
     * @return
     */
    @RequestMapping(value = "/updateCourseRelayInfo.user", method = RequestMethod.GET)
    @ResponseBody
    public ActResultDto updateCourseRelayInfo(String courseId,String isRelay,String relayCharge,String relayScale){
        ActResultDto actResultDto = new ActResultDto();
        if (courseId == null) {
            actResultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return actResultDto;
        }
        actResultDto=courseService.updateCourseRelayInfo(courseId, isRelay, relayCharge, relayScale);
        return actResultDto;
    }

    /**
     * 获取所有可以转播的课程，支持分页
     * @param offset
     * @param pageSize
     * @param sc
     * @param sort
     * @return
     */
    @RequestMapping(value = "/queryCanBroadcastCoursePage.user", method = RequestMethod.GET)
    @ResponseBody
    public ActResultDto queryCanBroadcastCoursePage(Integer offset, Integer pageSize,HttpServletRequest request,
                                                    @ApiParam(required = true, name = "查询", value = "名称") String courseName,
                                                    @ApiParam(required = true, name = "排序", value = "正序--0 , 倒序--1") String sc,
                                                    @ApiParam(required = true, name = "综合排序", value = "综合--0,时间--1,价格--2,分成比例--3,最新--4") String sort){
        ActResultDto actResultDto = new ActResultDto();
        if (offset == null) {
            offset = 0;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> list=courseService.queryCanBroadcastCoursePage(courseName,token.getId(),sc,sort,offset,pageSize);
        actResultDto.setData(list);
        return actResultDto;
    }

}
