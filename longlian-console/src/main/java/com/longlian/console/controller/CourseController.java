package com.longlian.console.controller;

import cn.jpush.api.utils.StringUtils;
import com.github.pagehelper.StringUtil;
import com.huaxin.util.*;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.service.*;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.live.service.*;
import com.longlian.live.util.PicUtil;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.yunxin.YunXinUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.live.util.yunxin.YunxinUserUtil;
import com.longlian.model.*;
import com.longlian.token.AppUserIdentity;
import com.longlian.token.ConsoleUserIdentity;
import com.longlian.type.LogType;
import com.longlian.type.ReturnMessageType;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by pangchao on 2017/2/15.
 */
@RequestMapping("/course")
@Controller
public class CourseController {
    private static Logger log = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    CourseService courseService;
    @Autowired
    private ChatRoomMsgService chatRoomMsgService;
    @Autowired
    private YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    private YunxinUserUtil yunxinUserUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LiveChannelService qiNiuliveChannelService;
    @Autowired
    AppUserService appUserService;
    @Autowired
    AvatarService avatarService;
    @Autowired
    CourseAvatarUserService courseAvatarUserService;
    @Autowired
    PicUtil picUtil;
    @Autowired
    AppMsgService appMsgService;
    @Autowired
    OrdersService ordersService;
    @Autowired
    CourseBaseService courseBaseService;



    @RequestMapping(value = "/getCourseOrdersList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getCourseOrdersList(DataGridPage requestModel, @RequestParam Map map) {
        DatagridResponseModel drm = new DatagridResponseModel();
        List<Map> courseOrdersList=courseService.getCourseOrdersList(requestModel, map);
        drm.setRows(courseOrdersList);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    /**
     * 课程统计报表导出
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.GET)
    @ResponseBody
    public void importExcel(HttpServletRequest req, HttpServletResponse response, String liveTopic,
                            String appUserName, String status, String isSerier,
                            String isFree, Date startDate,Date endDate,String isRelay,
                            @RequestParam(value ="sort", required = false)String sort,
                            @RequestParam(value = "order", required = false)String orderType) throws IOException {
        Map requestMap=new HashMap<>();
        if(status!=null){
            requestMap.put("status",status);
        }if(isSerier!=null){
            requestMap.put("isSerier",isSerier);
        }if(isFree!=null){
            requestMap.put("isFree",isFree);
        }if(isRelay!=null){
            requestMap.put("isRelay",isRelay);
        }
        requestMap.put("liveTopic",liveTopic);requestMap.put("appUserName",appUserName);


       // requestMap.put("startDate",startDate); requestMap.put("endDate",endDate);
      /*  if(sort!=null && !"".equals(sort) ){
            requestMap.put("sort",sort);
        }
        requestMap.put("orderType",orderType);*/
        ExportExcelWhaUtil exportExcelWhaUtil = courseService.importExcel(req,response,requestMap,startDate,endDate);
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=courseStatistics.xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            sos.write(FileUtils.readFileToByteArray(new File(path)));
            sos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 课程统计
     * 本节课平台流水页面
     * @author:liu.na
     * @return
     */
    @RequestMapping("/getPlatformStream")
    public ModelAndView getPlatformStream(long id) {
        ModelAndView view = new ModelAndView("/func/accountTrack/platformStreamList");
        view.addObject("courseId", id);
        //查询课程总收益
        Map map=new HashMap<>();
        map.put("courseId", id);
        Map data=courseService.getTeacherCourseProfit(map);
        view.addObject("data",data);
        return view;
    }

    /**
     * 课程统计 页面
     * @return
     */
    @RequestMapping(value = "/platformStreamImportExcel", method = RequestMethod.GET)
    @ResponseBody
    public void platformStreamImportExcel(HttpServletRequest req, HttpServletResponse response,String trackName,Long courseId,
                                          Date beginTime,Date endTime,String mobile) throws IOException {
        Map requestMap=new HashMap<>();
        requestMap.put("trackName", trackName);requestMap.put("courseId",courseId);
      //  requestMap.put("beginTime",beginTime);requestMap.put("endTime",endTime);
        requestMap.put("mobile", mobile);
        ExportExcelWhaUtil exportExcelWhaUtil = courseService.platformStreamImportExcel(req,response,requestMap,beginTime,endTime);
        OutputStream sos = response.getOutputStream();
        try {
            String path = exportExcelWhaUtil.getExcel();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=courseStreamList.xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            sos.write(FileUtils.readFileToByteArray(new File(path)));
            sos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping("/courseStatistics")
    public ModelAndView courseStatistics() {
        ModelAndView view = new ModelAndView("/func/accountTrack/courseStatistics");
        return view;
    }

    /**
     * 平台流水统计  导出页面
     */

    /**
     * 获取本节课流水列表
     * @author: liu.na
     */
    @RequestMapping(value = "/getPlatformStreamList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getPlatformStreamList(DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(courseService.getCoursePlatformStreamList(requestModel,map));
        model.setTotal(requestModel.getTotal());
        return model;
    }
    /**
     * 单节课页面
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/func/course/index");
        return view;
    }

    @RequestMapping("/channelCountIndex")
    public ModelAndView channelCountIndex() {
        ModelAndView view = new ModelAndView("/func/shareChannel/channelCountIndex");
        return view;
    }

    /**
     * 课程渠道统计
     */
    @RequestMapping(value = "/getListChannelSimplePage", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getListChannelSimplePage(@RequestParam(value ="sort", required = false)String sort,
                                         @RequestParam(value = "order", required = false)String orderType,
                                         DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        if(Utility.isNullorEmpty(map.get("sortType"))){
            map.put("sortType","1");
        }
        if(sort!=null && !"".equals(sort) ){
            String field =  com.huaxin.util.StringUtil.propertyToField(requestModel.getSort());
            map.put("sort",field);
        }
        map.put("orderType",orderType);
        model.setRows(courseService.getListChannelSimplePage(requestModel, map));
        model.setTotal(requestModel.getTotal());
        return model;
    }

    /**
     * 获取单节课列表
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getList(@RequestParam(value ="sort", required = false)String sort,
                                         @RequestParam(value = "order", required = false)String orderType,
                                         DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        if(Utility.isNullorEmpty(map.get("sortType"))){
            map.put("sortType","1");
        }
        if(sort!=null && !"".equals(sort) ){
            String field =  com.huaxin.util.StringUtil.propertyToField(requestModel.getSort());
            map.put("sort",field);
        }
        map.put("orderType",orderType);
        model.setRows(courseService.getListPage(requestModel, map));
        model.setTotal(requestModel.getTotal());
        return model;
    }


    /**
     * 跳转至查看审核意见页面
     *
     * @param id
     * @return
     */
    @RequestMapping("/details")
    public ModelAndView details(long id) {
        ModelAndView model = new ModelAndView("/func/course/details");
        Map map = courseService.getCourseDetails(id);
        model.addObject("course", map);
        model.addObject("id", id);
        return model;
    }


    /**
     * 跳转至课程编辑页面
     *
     * @param id
     * @return
     */
    @RequestMapping("/editCourse")
    public ModelAndView editCourse(long id) {
        ModelAndView model = new ModelAndView("/func/course/editCourse");
        Course course = courseService.getCourse(id);
        List<CourseWare> CourseWareList=courseService.getCourseWare(id);
        model.addObject("course", course);
        model.addObject("CourseWareList", CourseWareList);
        model.addObject("id", id);
        return model;
    }

    /**
     * 根据id进行查询
     */
    @RequestMapping(value = "/findByIdForEdit", method = RequestMethod.GET)
    @ResponseBody
    public ActResult findByIdForEdit(long id) {
        ActResult result = new ActResult();
        Course course = courseService.getCourse(id);
        result.setData(course);
        return result;
    }

    /**
     * 获取简介图片
     */
    @RequestMapping(value = "/getCourseImglist", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getCourseWarelist(long id) {
        ActResult result = new ActResult();
        List<CourseImg> CourseImglist = courseService.getCoursImg(id);
        result.setData(CourseImglist);
        return result;
    }
    
    /**
     * 根据id进行查询
     */
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    @ResponseBody
    public ActResult findById(long id) {
        ActResult result = new ActResult();
        CourseDto course = courseService.findById(id);
        result.setData(course);
        return result;
    }

    /**
     * 修改
     *
     * @param course
     * @return
     */
    @RequestMapping(value = "/doUpdate", method = RequestMethod.POST)
    @ResponseBody
    public ActResult doUpdate(@RequestBody CourseDto course) throws Exception {
        //课程封面
            course.setCoverssAddress(picUtil.base64ToUrl(course.getCoverssAddress()));
        //列表图片
          //  course.setColImgAddress(picUtil.base64ToUrl(course.getColImgAddress()));
        ActResult result = new ActResult();
        try {
            courseService.doUpdate(course);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            log.error("保存失败");
        }
        String courseKey = RedisKey.ll_course + course.getId();
        redisUtil.del(courseKey);
        return result;

    }

    /**
     * 获取课程评价列表
     */
    @RequestMapping(value = "/getCourseCommentList", method = RequestMethod.GET)
    @ResponseBody
    public ActResult getCourseCommentList(long id) {
        ActResult result = new ActResult();
        List<Map> list = courseService.getCourseCommentList(id);
        result.setData(list);
        return result;
    }

    /**
     * 下架
     */
    @RequestMapping(value = "/updateDown", method = RequestMethod.GET)
    @ResponseBody
    public ActResult updateDown(HttpServletRequest request , long id) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            if(id>=SystemCofigConst.RELAY_COURSE_ID_SIZE){
                //下架转播课
                courseService.updateRelayDown(id, token.getId(), token.getName());
            }else{
                courseService.updateDown(id, token.getId(), token.getName());
                Course course = courseBaseService.getCourseFromRedis(id);
                if (course != null) {
                    appMsgService.updateMsgCourseStatus(id, "1" , course.getRoomId());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            log.error("下架失败");
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }
    /**
     * 恢复
     */
    @RequestMapping(value = "/recoveryCourse", method = RequestMethod.GET)
    @ResponseBody
    public ActResult recoveryCourse(HttpServletRequest request , long id) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            courseService.recoveryCourse(id, token.getId(), token.getName());
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("恢复失败");
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }
    
    /**
     * 删除
     */
    @RequestMapping(value = "/del", method = RequestMethod.GET)
    @ResponseBody
    public ActResult del(HttpServletRequest request , long id , String isDel) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            Course course = courseBaseService.getCourseFromRedis(id);
            if ("1".equals(isDel)) {
                courseService.del(id, token.getId() , token.getName());
                if (course != null) {
                    appMsgService.updateMsgCourseStatus(id, "1", course.getRoomId());
                }
            } else {
                courseService.restore(id, token.getId(), token.getName());
                if (course != null) {
                    appMsgService.updateMsgCourseStatus(id, "0", course.getRoomId());
                }
            }
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("操作失败");
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }
    /**
     * 上线
     */
    @RequestMapping(value = "/updateUp", method = RequestMethod.GET)
    @ResponseBody
    public ActResult updateUp(HttpServletRequest request , long id) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            if(id>=SystemCofigConst.RELAY_COURSE_ID_SIZE){
                //转播课上架
                courseService.updateRelayUp(id, token.getId(), token.getName());
            }else{
                courseService.updateUp(id, token.getId(), token.getName());
                Course course = courseBaseService.getCourseFromRedis(id);
                if (course != null) {
                    appMsgService.updateMsgCourseStatus(id, "0" , course.getRoomId());
                }
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
     * 修改自动关闭时间
     */
    @RequestMapping(value = "/updateAutoCloseTime", method = RequestMethod.GET)
    @ResponseBody
    public ActResult updateAutoCloseTime(HttpServletRequest request , long id , long updateValue) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            result.setData(courseService.setAutoCloseTime(id, updateValue, token.getId(), token.getName()));
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return result;
    }
    
    
    /**
     * 修改访问人数
     */
    @RequestMapping(value = "/addVisitCount", method = RequestMethod.GET)
    @ResponseBody
    public ActResult addVisitCount(HttpServletRequest request , long id , long addCount) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            result.setData(courseService.addVisitCount(id, addCount, token.getId(), token.getName()));
        } catch (Exception e) {
            result.setSuccess(false);
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }

    /**
     * 修改参加人数
     */
    @RequestMapping(value = "/addJoinCount", method = RequestMethod.GET)
    @ResponseBody
    public ActResult addJoinCount(HttpServletRequest request , long id , long addCount) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            result.setData(courseService.addJoinCount(id, addCount, token.getId(), token.getName()));
        } catch (Exception e) {
            result.setSuccess(false);
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }



    /**
     * 跳转至查看审核意见页面
     *
     * @param id
     * @return
     */
    @RequestMapping("/live")
    public ModelAndView live(HttpServletRequest request, long id) {
        ConsoleUserIdentity token = (ConsoleUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ModelAndView model = new ModelAndView("func/course/live");
        Course course = courseService.getCourse(id);
        //是语音直播
        if ("1".equals(course.getLiveWay())) {
            model = new ModelAndView("func/course/voice");
        }
        model.addObject("courseId", id);
        return model;
    }
    @Autowired
    EndLiveService endLiveService;
    /**
     *结束直播
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/end", method = RequestMethod.GET)
    @ResponseBody
    public ActResult end(HttpServletRequest request , long id  ) throws Exception {
        ActResult result = new ActResult();
        Course course = courseService.getCourse(id);
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        try {
            endLiveService.endLive(course);
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("结束直播失败");
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        SystemLogUtil.saveSystemLog(LogType.course_console_end.getType(), "1", course.getAppId(), String.valueOf(course.getAppId()), String.valueOf(course.getId()), "课程：" + course.getLiveTopic() + "已被后台管理员：" + token.getName() + "(id:" + token.getId() + ")关闭");
        return result;
    }


    /**
     *  
     *
     * @param id
     * @return
     */
    @RequestMapping("/virtualUser")
    public ModelAndView virtualUser(HttpServletRequest request, long id , Long userId , String name , String isInRoom) {
        ConsoleUserIdentity token = (ConsoleUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ModelAndView model = new ModelAndView("func/course/virtualUser");
        Course course = courseService.getCourse(id);


        model.addObject("courseId", id);
        model.addObject("userId", userId);
        model.addObject("name", name);
        model.addObject("isInRoom", isInRoom);
        return model;
    }

    /**
     * 取得课程相关信息
     *
     * @param request
     * @param courseId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getCourseByVirtualUser")
    @ResponseBody
    public ActResultDto getCourseByVirtualUser(HttpServletRequest request, Long courseId ,Long userId , String isInRoom) throws Exception {
        ConsoleUserIdentity token = (ConsoleUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();

        Map result = new HashMap();
        Course course = courseService.getCourse(courseId);
        //LiveChannel liveChannel = liveChannelService.getCourseLiveAddr(course);
//        String playAddr1 = "";
//        String playAddr3 = "";
        //根据取得的直播通道,引出播放地址
//        if (liveChannel != null) {
//            playAddr1 = liveChannel.getPlayAddr1();
//            playAddr3 = liveChannel.getPlayAddr3();
//        }
//        course.setHlsLiveAddress(playAddr3);
//        course.setLiveAddress(playAddr1);
        //设置云信聊天室相关
        //不管，他登录聊天室没有,先退出一次
        if ("1".equals(isInRoom)) {
           // avatarService.removeRobootByUserId(String.valueOf(courseId) , userId , true);
        }
        //增加登录记录
       //courseAvatarUserService.insertUser(courseId ,  userId);

        Map chatRoom = new HashMap();
        //云信appkey
        chatRoom.put("appKey", YunXinUtil.appKey);
        AppUser appUser = appUserService.getAppUserById(userId);
        String yunxinToken = avatarService.createYunxinToken(appUser);
        //chatRoom.put("appSecret", YunXinUtil.appSecret);
        //根据云信accid和云信聊天室ID，取得聊天室的地址(是个数组)
        chatRoom.put("roomAddr", yunxinChatRoomUtil.getChatRoomAddress(String.valueOf(userId), String.valueOf(course.getChatRoomId())));
        chatRoom.put("chatRoomId", course.getChatRoomId());
        chatRoom.put("userId", String.valueOf(userId));
        chatRoom.put("yunxinToken", yunxinToken);

        result.put("course", course);
        result.put("chatRoom", chatRoom);
        //result.put("courseWare", courseService.getCourseWare(courseId));


        return ActResultDto.success().setData(result);
    }

    /**
     * 取得课程相关信息
     *
     * @param request
     * @param courseId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getCourse")
    @ResponseBody
    public ActResultDto getCourse(HttpServletRequest request, Long courseId) throws Exception {
        ConsoleUserIdentity token = (ConsoleUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();

        String admin = "a_" + String.valueOf(token.getId());
        Map result = new HashMap();
        Course course = courseService.getCourse(courseId);
        LiveChannel liveChannel = qiNiuliveChannelService.getCourseLiveAddr(course);
        String playAddr1 = "";
        String playAddr3 = "";
        //根据取得的直播通道,引出播放地址
        if (liveChannel != null) {
            playAddr1 = liveChannel.getPlayAddr1();
            playAddr3 = liveChannel.getPlayAddr3();
        }
        course.setHlsLiveAddress(playAddr3);
        course.setLiveAddress(playAddr1);
        //设置成管理员
        yunxinChatRoomUtil.setRole(String.valueOf(course.getChatRoomId()), String.valueOf(course.getAppId()), admin, 1, "true");
        //设置云信聊天室相关
        Map chatRoom = new HashMap();
        //云信appkey
        chatRoom.put("appKey", YunXinUtil.appKey);
        //chatRoom.put("appSecret", YunXinUtil.appSecret);
        //根据云信accid和云信聊天室ID，取得聊天室的地址(是个数组)
        chatRoom.put("roomAddr", yunxinChatRoomUtil.getChatRoomAddress(admin, String.valueOf(course.getChatRoomId())));
        chatRoom.put("chatRoomId", course.getChatRoomId());
        chatRoom.put("userId", admin);
        chatRoom.put("yunxinToken", token.getYunxinToken());


        result.put("course", course);
        result.put("chatRoom", chatRoom);
        result.put("courseWare", courseService.getCourseWare(courseId));


        return ActResultDto.success().setData(result);
    }

    /**
     * 取得历史消息
     *
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/getHistoryMsg")
    @ResponseBody
    public ActResultDto getHistoryMsg(HttpServletRequest request, Long courseId) {
        //AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> result = chatRoomMsgService.getHistoryMsgByCourseId(courseId);
        return ActResultDto.success().setData(result);
    }

    /**
     * 禁止某条流的推送
     *
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/forbidLiveStream")
    @ResponseBody
    public ActResultDto forbidLiveStream(HttpServletRequest request, Long courseId) throws Exception {
        //AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Course course = courseService.getCourse(courseId);
        LiveChannel liveChannel = qiNiuliveChannelService.getCourseLiveAddr(course);
        if (liveChannel == null) {
            return ActResultDto.fail(ReturnMessageType.LIVE_NO_START.getCode());
        }

        qiNiuliveChannelService.forbidLiveStream(liveChannel, course);
        return ActResultDto.success();
    }


    /**
     * 恢复某条流的推送
     *
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/resumeLiveStream")
    @ResponseBody
    public ActResultDto resumeLiveStream(HttpServletRequest request, Long courseId) throws Exception {
        //AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Course course = courseService.getCourse(courseId);
        LiveChannel liveChannel = qiNiuliveChannelService.getCourseLiveAddr(course);
        if (liveChannel == null) {
            return ActResultDto.fail(ReturnMessageType.LIVE_NO_START.getCode());
        }
        qiNiuliveChannelService.resumeLiveStream(liveChannel, course);
        return ActResultDto.success();
    }


    /**
     * 跳转页面
     * @return
     */
    @RequestMapping("/toGetCourseMessageList")
    public ModelAndView toGetCourseMessageList(){
        ModelAndView view = new ModelAndView("/func/course/roomCourseMsg");
        return view;
    }


    @RequestMapping(value = "/getCourseMessageList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getCourseMessageList(DataGridPage requestModel,String courseId,String liveTopic,String attach) {
        DatagridResponseModel drm = new DatagridResponseModel();
        Long id = null;
        if (!StringUtils.isEmpty(courseId)) id= Long.parseLong(courseId);
        drm.setRows(courseService.getCourseMessageListPage(requestModel, id,liveTopic,attach));
        drm.setTotal(requestModel.getTotal());
        return drm;
    }
    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    @ResponseBody
    public ActResult deleteById(long id) {
        ActResult result = new ActResult();
        try{
            courseService.deleteById(id);

            Course course = courseBaseService.getCourseFromRedis(id);
            if (course != null) {
                appMsgService.updateMsgCourseStatus(id, "1" , course.getRoomId());
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            log.error("删除异常");
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }



    /**
     * 处理app_msg中tbale_id为course_id  type = 3 14  ROOM_ID IS BULL
     * )
     * @return
     */
    @RequestMapping("/setAppMsgCourseId")
    @ResponseBody
    public ActResult setAppMsgCourseId(){
        ActResult result = new ActResult();
//        List<AppMsg> list = appMsgService.findOrderId();
//        for(AppMsg appMsg : list){
//            Long courseId = ordersService.findCourseId(appMsg.getTableId());
//            if(courseId != null){
//                appMsgService.updateTableId(appMsg.getId(),courseId);
//            }
//        }
        return result;
    }

    /**
     * 跳转至课程编辑页面
     *
     * @param id
     * @return
     */
    @RequestMapping("/recommendTo")
    public ModelAndView recommendTo(long id) {
        ModelAndView model = new ModelAndView("/func/recoCourse/recommendTo");
        model.addObject("id",id);
        return model;
    }

    /**
     * 处理app_msg中新添加字段设置
     * @return
     */
    @RequestMapping("/dealCourseCount")
    @ResponseBody
    public ActResult dealCourseCount(HttpServletRequest request) throws Exception {
        ActResult result = new ActResult();
        List<Map> list = courseService.getNeedDealCourse();
        for(Map course : list){
            Long courseId = (Long)course.get("courseId");


                Integer count1 = (Integer)course.get("count1");
                Integer count2 = (Integer)course.get("count2");
                Long count3 = (Long)course.get("count3");
                Long count4 = (Long)course.get("count4");

                if (count3 > count1) {
                    this.addVisitCount(request , courseId , count3 - count1);
                }
                if (count4 > count2) {
                    this.addJoinCount(request , courseId , count4 - count2);
                }

        }
        return result;
    }
    /**
     * 修改评分
     */
    @RequestMapping(value = "/addRecoSort", method = RequestMethod.GET)
    @ResponseBody
    public ActResult addRecoSort(HttpServletRequest request , long id , long addCount) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            result.setData(courseService.addRecoSort(id, addCount, token.getId(), token.getName()));
        } catch (Exception e) {
            result.setSuccess(false);
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }

    /**
     * 修改 分类评分
     */
    @RequestMapping(value = "/addTypeSort", method = RequestMethod.GET)
    @ResponseBody
    public ActResult addTypeSort(HttpServletRequest request , long id , BigDecimal addCount) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            result.setData(courseService.addTypeSort(id, addCount, token.getId(), token.getName()));
        } catch (Exception e) {
            result.setSuccess(false);
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }

    @RequestMapping(value = "/replaceAddress")
    public void replaceAddress(){
        Long pageSize = 1000l;
        Long offset = 0l;
        long size = 0;
        do {
            List<Course> courseList = courseService.findAllCourse(pageSize , offset);
            size = courseList.size();
            for(Course course : courseList){
                String videoAddress = course.getVideoAddress();
                String dbAddress = course.getVideoAddress();
                if(StringUtils.isNotEmpty(videoAddress)){
                    videoAddress = videoAddress.replace("https://longlian-output.oss-cn-beijing.aliyuncs.com" , "http://file3.llkeji.com");
                    videoAddress = videoAddress.replace("http://longlian-live2.oss-cn-shanghai.aliyuncs.com" , "http://file2.llkeji.com");
                    if(!dbAddress.equals(videoAddress)){
                        course.setVideoAddress(videoAddress);
                        courseService.updateVideoAddress(course.getId(), course.getVideoAddress());
                        redisUtil.del(RedisKey.ll_course + course.getId());
                    }
                }
            }
            offset = offset + courseList.size();
        }while (size == pageSize);
    }
    /**
     * 获取单节课列表
     */
    @RequestMapping(value = "/getListByRoomId", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getListByRoomId(DatagridRequestModel requestModel,@RequestParam Map map) {
        DatagridResponseModel model = new DatagridResponseModel();
        if(Utility.isNullorEmpty(map.get("sortType"))){
            map.put("sortType","1");
        }
        model.setRows(courseService.getListByRoomIdPage(requestModel, map));
        model.setTotal(requestModel.getTotal());
        return model;
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/OSSdel", method = RequestMethod.GET)
    @ResponseBody
    public ActResult OSSdel(HttpServletRequest request , long id) throws Exception {
        ActResult result = new ActResult();
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            Course course = courseBaseService.getCourseFromRedis(id);
            courseService.delOSS(course, token.getId() , token.getName());
            if (course != null) {
                appMsgService.updateMsgCourseStatus(id, "1", course.getRoomId());
            }
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("操作失败");
        }
        String courseKey = RedisKey.ll_course + id;
        redisUtil.del(courseKey);
        return result;
    }

    /*
    * 批量删除OSS
   */
    @RequestMapping(value = "/delOSSAll", method = RequestMethod.POST)
    @ResponseBody
    public ActResult delOSSAll(HttpServletRequest request,Optional<Integer> yuefen) throws Exception {
        ActResult result = new ActResult();
        if(yuefen.isPresent()){
            Integer integer = yuefen.get();
            if(integer>0){
                try {
                    ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
                    List<Course> courses = courseBaseService.getCourses(integer);
                    if(courses!=null&&courses.size()>0){
//                        appMsgService.updateMsgCourseStatues(courses);
                        for(Course course:courses){
                            if (course != null) {
                                String courseKey = RedisKey.ll_course + course.getId();
                                redisUtil.del(courseKey);
                            }

                        }
                        courseService.delOSSAll(courses, token.getId(), token.getName());
                    }
                } catch (Exception e) {
                    result.setSuccess(false);
                    log.error("操作失败");
                }

            }
        }
        return result;
    }

    /**
     * 设置单节课管理 首页显示隐藏功能
     */
    @RequestMapping(value = "/showHide", method = RequestMethod.GET)
    @ResponseBody
    public ActResult showHide(HttpServletRequest request , long id,String isHide) throws Exception {
        ActResult result = new ActResult();
        try {
            if(Utility.isNullorEmpty(id)){
                result.setSuccess(false);
                result.setMsg("ID不能为空");
                return result;
            }
            if(Utility.isNullorEmpty(isHide)){
                result.setSuccess(false);
                result.setMsg("显示隐藏值不能为空");
                return result;
            }
            Course course = new Course();
            course.setId(id);
            course.setIsHide(isHide);
            courseService.updateShowHide(course);
            result.setSuccess(true);
            result.setMsg("设置显示隐藏功能成功");
            result.setCode(200);
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("设置显示隐藏功能失败");
        }
        return result;
    }
    
    /**
     * 设置单节课管理 转播市场显示隐藏功能
     */
    @RequestMapping(value = "/showRelayHide", method = RequestMethod.GET)
    @ResponseBody
    public ActResult showRelayHide(HttpServletRequest request , long id,String isRelayHide) throws Exception {
        ActResult result = new ActResult();
        try {
            if(Utility.isNullorEmpty(id)){
                result.setSuccess(false);
                result.setMsg("ID不能为空");
                return result;
            }
            if(Utility.isNullorEmpty(isRelayHide)){
                result.setSuccess(false);
                result.setMsg("显示隐藏值不能为空");
                return result;
            }
            Course course = new Course();
            course.setId(id);
            course.setIsRelayHide(isRelayHide);
            courseService.updateShowRelayHide(course);
            result.setSuccess(true);
            result.setMsg("设置转播显示隐藏功能成功");
            result.setCode(200);
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("设置转播显示隐藏功能失败");
        }
        return result;
    }
    
}