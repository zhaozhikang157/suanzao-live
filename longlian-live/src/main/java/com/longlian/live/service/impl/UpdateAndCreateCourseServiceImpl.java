package com.longlian.live.service.impl;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.StringUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.live.dao.CourseImgMapper;
import com.longlian.live.dao.CourseMapper;
import com.longlian.live.dao.CoursePrivateCardMapper;
import com.longlian.live.dao.CourseWareMapper;
import com.longlian.live.interceptor.UpdateSeriesCourseTime;
import com.longlian.live.interceptor.UpdateSeriesCourseTimeInterceptor;
import com.longlian.live.service.*;
import com.longlian.model.*;
import com.longlian.model.course.CourseCard;
import com.longlian.type.ReturnMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by liuhan on 2017-10-31.
 */
@Service("updateAndCreateCourseService")
public class UpdateAndCreateCourseServiceImpl implements UpdateAndCreateCourseService {

    @Autowired
    CourseMapper courseMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    CourseImgMapper courseImgMapper;
    @Autowired
    AppUserService appUserService;
    @Autowired
    WechatOfficialService wechatOfficialService;
    @Autowired
    CountService countService;
    @Autowired
    CourseWareMapper courseWareMapper;
    @Autowired
    LiveRoomService liveRoomService;
    private static Logger log = LoggerFactory.getLogger(UpdateAndCreateCourseServiceImpl.class);
    @Autowired
    CourseAuditService courseAuditService;
    @Autowired
    CourseRelayService courseRelayService;
    @Autowired
    VideoService videoService;
    @Autowired
    private CoursePrivateCardMapper coursePrivateCardMapper;
    @Autowired
    private LiveChannelService qiNiuliveChannelService;
    @Override
    public ActResultDto createCourse(CourseDto course) throws Exception {
        ActResultDto ac = new ActResultDto();
        try {
            //将图片设置为竖屏直播的背景图
            course.setVerticalCoverssAddress(course.getCoverssAddress());
            //设置课程的默认值
            setCourseDefaultValue(course);
            //如果不是录播课程
            if("0".equals(course.getIsRecorded())){
                //判断，这节课开课时间范围内是否有其它开播课程
                List<Course> courseLivingList =  courseMapper.getshortTimeCourse(course.getRoomId());
                if(courseLivingList.size()>0){
                    for(Course liveCourse : courseLivingList) {
                        if (Utility.isNullorEmpty(liveCourse.getStartTime())) {
                            continue;
                        }
                        Date beforeTime = DateUtil.parseDate("yyyy-MM-dd hh:mm:ss", DateUtil.getTimeByMinute(liveCourse.getStartTime(), -11));
                        Date afterTime = DateUtil.parseDate("yyyy-MM-dd hh:mm:ss", DateUtil.getTimeByMinute(liveCourse.getStartTime(), 11));
                        //如有在10分钟范围内的，则不让创建课程，让修改开课时间
                        if (beforeTime.getTime() < course.getStartTime().getTime() && course.getStartTime().getTime() < afterTime.getTime()) {
                            ac.setCode(ReturnMessageType.CREATE_COURSE_TIME_SHORT.getCode());
                            ac.setMessage(ReturnMessageType.CREATE_COURSE_TIME_SHORT.getMessage());
                            return ac;
                        }
                    }
                }
            }
            //如果是系列单节课，设置系列课开始时间
            if(course.getSeriesCourseId() > 0){
                Course c = new Course();
                c.setStartTime(course.getStartTime());
                c.setId(course.getSeriesCourseId());
                courseMapper.updateSeriesStartTime(c);
            }
            //如果是录播的课,设置下线状态
            if ("1".equals(course.getIsRecorded())) {
                course.setStatus("1");
            } else {
                course.setStatus("0");
            }
            if(course.getIsRelay() == 1){
                course.setIsOpened(1);
                course.setSetRelayTime(new Date());
            }
            //如果是语音课，先隐藏状态
            if("1".equals(course.getLiveWay())){
                course.setIsHide("1");
            }
            courseMapper.insert(course);
            //如果是系列单节课，设置系列课开始时间
            //如果是录播，需要把创建课的标志打起，方便后面判断
            if ("1".equals(course.getIsRecorded())) {
                redisUtil.setex(RedisKey.ll_is_create_course + course.getId(), 60 * 60 * 24, "1");
            }
            addWareAndImg(course, course.getCoursePhoto(), course.getCourseImg());
            long courseId = course.getId();
            //设置聊天室视频直播相关属性
            courseBaseService.setLiveRoomInfo(course);
            String imgContent = course.getCourseContent();
            StringBuffer contents = new StringBuffer();
            if (!StringUtils.isEmpty(imgContent)) {
                //courseContent=[{"img":"http://www.baidu.com" , "content":"dafdfa"},{"img":"http://www.baidu.com" , "content":"dafdfa"}]
                List<HashMap> list = JsonUtil.getList(imgContent, HashMap.class);
                List<Map<Long, String>> strListMap = new ArrayList<Map<Long, String>>();
                for (int i = 0; i < list.size(); i++) {
                    Map m = list.get(i);
                    String image = (String) m.get("img");
                    String content = (String) m.get("content");
                    String localId = (String) m.get("localId");
                    if (("null".equals(image))) {
                        image = "";
                    }
                    if (StringUtils.isEmpty(image) && StringUtils.isEmpty(content)) {
                        continue;
                    }
                    CourseImg img = new CourseImg();
                    img.setCourseId(course.getId());
                    img.setContent(content);
                    img.setAddress(image);
                    img.setOrderSort(i);
                    img.setCreateTime(new Date());
                    img.setStatus("0");
                    img.setLocalIds(localId);
                    courseImgMapper.insert(img);
                    if (!image.startsWith("http")) {
                        Map<Long, String> map = new HashMap<Long, String>();
                        map.put(img.getId(), image);
                        strListMap.add(map);
                    }
                    contents.append(content);
                }
            }

            this.sendCourseMqMsg(course);
            //微信发送消息
            AppUser teach = appUserService.getById(course.getAppId());
            course.setAppUserName(teach.getName());
            //录播的课程，应该是审核通过后发送
            if (!"1".equals(course.getIsRecorded())) {
                wechatOfficialService.getSendWechatTemplateMessageSaveRedis(course);
            }
            Map map = new HashMap<>();
            map.put("courseId", course.getId());
            map.put("seriesCourseId", course.getSeriesCourseId());
            ac.setData(map);

            if (course!=null && "1".equals(course.getIsRecorded())) {
                //初始化审核记录
                CourseAudit courseAudit = new CourseAudit();
                courseAudit.setStatus("0");
                courseAudit.setCourseId(course.getId());
                courseAudit.setCreateTime(new Date());
                courseAuditService.insert(courseAudit);
            }

            dealRecordInfo(course, contents.toString());
            redisUtil.del(RedisKey.teacher_course_count + course.getAppId());
            redisUtil.del(RedisKey.series_course_count + course.getRoomId());
            qiNiuliveChannelService.create(String.valueOf(course.getId()));
        } catch (Exception e) {
            log.error("创建单个课程失败！");
            throw e;
        }

        return ac;
    }

    /**
     * 处理录播课程相关信息
     *
     * @param course
     * @param contents
     */
    public void dealRecordInfo(Course course, String contents) {
        //如果是录播的课,设置一直处于连接状态
        //发送签别消息
        if ("1".equals(course.getIsRecorded())) {
            redisUtil.set(RedisKey.ll_course_live_connected + course.getId(), "1");
            Map map = new HashMap();
            map.put("courseId", String.valueOf(course.getId()));
            map.put("content", contents.toString());
            map.put("liveTopic", course.getLiveTopic());
            map.put("remark", course.getRemark());
            redisUtil.lpush(RedisKey.ll_course_garbage, JsonUtil.toJson(map));
        }
    }

    /**
     * 发送消息
     *
     * @param course
     */
    @UpdateSeriesCourseTime
    public void sendCourseMqMsg(Course course) {
        //录播的课程，应该是审核通过后发送
        if (!"1".equals(course.getIsRecorded())) {
            //如果是系列课里面的单节课，则发送更新系列课时间队列
            if (course.getSeriesCourseId() != 0) {
                UpdateSeriesCourseTimeInterceptor.setSeriesId(course.getSeriesCourseId());
            } else {
                //只统计单节课
                countService.newCourseCount(course.getId());
            }
        }

        Map map = new HashMap();
        map.put("courseId", course.getId());
        map.put("image", course.getVerticalCoverssAddress());
        redisUtil.lpush(RedisKey.ll_set_vertical_coverss_img, JsonUtil.toJson(map));

    }

    /**
     * 添加 课件 和 图片简介
     *
     * @param course
     * @param photos
     * @param imgs
     */
    private void addWareAndImg(Course course, String photos, String imgs) {
        //批量添加课件图片
        if (!StringUtils.isEmpty(photos)) {
            String[] ps = photos.split(";");
            List list = Arrays.asList(ps);
            courseWareMapper.insertList(list, course.getId());
        }

        //批量添加简介图片
        if (!StringUtils.isEmpty(imgs)) {
            String[] ps = imgs.split(";");
            List list = Arrays.asList(ps);
            courseImgMapper.insertList(list, course.getId());
        }
    }

    /**
     * 设置默认值
     *
     * @param course
     */
    private void setCourseDefaultValue(Course course) {
        if("1".equals(course.getLiveWay()) || "1".equals(course.getIsRecorded())){
            course.setLiveAddress("");
        }

        /*if (Utility.isNullorEmpty(course.getDivideScale())) {
            //如果前台没有传数据，分销比例则设置为0
            course.setDivideScale("0");
        }*/
        if (Utility.isNullorEmpty(course.getChargeAmt())) {
            course.setChargeAmt(new BigDecimal(0));
        }
        if (course.getRoomId() <= 0) {
            LiveRoom liveRoom = liveRoomService.findByAppId(course.getAppId());
            course.setRoomId(liveRoom.getId());
        }
        if (course.getIsSeriesCourse() == null) {
            course.setIsSeriesCourse("0");
        }
        if (course.getUpdatedCount() == null) {
            course.setUpdatedCount(0);
        }
        if (course.getCoursePlanCount() == null) {
            course.setCoursePlanCount(0);
        }
        if (course.getLiveType() == null) {
            course.setLiveType("0");
        }
        if ("2".equals(course.getLiveWay())) {
            course.setLiveWay("0");
            course.setIsVerticalScreen("0");
        } else if ("3".equals(course.getLiveWay())) {
            course.setLiveWay("0");
            course.setIsVerticalScreen("1");
        }
        //默认为1
        if (course.getIsVerticalScreen() == null) {
            course.setIsVerticalScreen("0");
        }
        //默认为0
        if (StringUtils.isEmpty(course.getIsRecorded())) {
            course.setIsRecorded("0");
        }
        //默认为0
        if (StringUtils.isEmpty(course.getTrySeeTime())) {
            course.setTrySeeTime(0l);
        }
        //默认为0
        if(StringUtils.isEmpty(course.getIsHide())){
            course.setIsHide("0");
        }
        //开始时间为null默认当前时间
        if(null==course.getStartTime()){
            course.setStartTime(new Date());
        }
    }


    @Override
    public ActResultDto createSeriesCourse(CourseDto course) throws Exception {
        ActResultDto ac = new ActResultDto();
        try {

            if (StringUtils.isEmpty(course.getIsRecorded())) {
                course.setIsRecorded("0");
            }
            //课程封面
//            course.setCoverssAddress(picUtil.base64ToUrl(course.getCoverssAddress()));
           /* if (Utility.isNullorEmpty(course.getDivideScale())) {
                //如果前台没有传数据，分销比例则设置为0
                course.setDivideScale("0");
            }*/

            if (Utility.isNullorEmpty(course.getChargeAmt())) {
                course.setChargeAmt(new BigDecimal(0));
            }

            if (course.getRoomId() <= 0) {
                LiveRoom liveRoom = liveRoomService.findByAppId(course.getAppId());
                course.setRoomId(liveRoom.getId());
            }
            course.setIsSeriesCourse("1");
            course.setUpdatedCount(0);
            //如果是录播的课,设置下线状态
            if ("1".equals(course.getIsRecorded())) {
                course.setStatus("1");
            } else {
                course.setStatus("0");
            }
            //如果设置转播 控制转播价格不可编辑
            if(course.getIsRelay() == 1){
                course.setIsOpened(1);
                course.setSetRelayTime(new Date());
            }
            if (Utility.isNullorEmpty(course.getCoursePlanCount())) {
                course.setCoursePlanCount(0);
            }
            //course.setCoursePlanCount(0);
            course.setLiveType("0");
            //course.setCoursePlanCount(0);
            courseMapper.insert(course);
            String imgContent = course.getCourseContent();
            StringBuffer contents = new StringBuffer();
            if (!StringUtils.isEmpty(imgContent)) {
                //courseContent=[{"img":"http://www.baidu.com" , "content":"dafdfa"},{"img":"http://www.baidu.com" , "content":"dafdfa"}]
                List<HashMap> list = JsonUtil.getList(imgContent, HashMap.class);
                List<Map<Long, String>> strListMap = new ArrayList<Map<Long, String>>();
                for (int i = 0; i < list.size(); i++) {
                    Map m = list.get(i);
                    String image = (String) m.get("img");
                    String content = (String) m.get("content");
                    String localId = (String) m.get("localId");
                    if (("null".equals(image))) {
                        image = "";
                    }
                    if (StringUtils.isEmpty(image) && StringUtils.isEmpty(content)) {
                        continue;
                    }
                    CourseImg img = new CourseImg();
                    img.setCourseId(course.getId());
                    img.setContent(content);
                    img.setAddress(image);
                    img.setOrderSort(i);
                    img.setCreateTime(new Date());
                    img.setStatus("0");
                    img.setLocalIds(localId);
                    courseImgMapper.insert(img);
                    if (!image.startsWith("http")) {
                        Map<Long, String> map = new HashMap<Long, String>();
                        map.put(img.getId(), image);
                        strListMap.add(map);
                    }
                    contents.append(content);
                }
            }

            ac.setData(course.getId());

            if (course!=null && "1".equals(course.getIsRecorded())) {
                //初始化审核记录
                CourseAudit courseAudit = new CourseAudit();
                courseAudit.setStatus("0");
                courseAudit.setCourseId(course.getId());
                courseAudit.setCreateTime(new Date());
                courseAuditService.insert(courseAudit);
            }

            dealRecordInfo(course, contents.toString());
            redisUtil.del(RedisKey.series_course_count + course.getRoomId());
            redisUtil.del(RedisKey.teacher_course_count + course.getAppId());
        } catch (Exception e) {
            //.setMessage("创建单个课程失败!!");
            log.error("创建系列课程失败！");
            throw e;
        }
        return ac;
    }


    @Override
    @UpdateSeriesCourseTime
    public void updateCourse(CourseDto course,HttpServletRequest request) throws Exception {
        //课程封面
//        course.setCoverssAddress(picUtil.base64ToUrl(course.getCoverssAddress()));
        //课件图片
        String photos = course.getCoursePhoto();
//        if (StringUtils.isEmpty(photos)) {
//            photos = getBase642Images(course.getCoursePhotos());
//        }
        //将图片设置为竖屏直播的背景图
        course.setVerticalCoverssAddress(course.getCoverssAddress());
        //简介图片
        String imgs = course.getCourseImg();
        String imgContent = course.getCourseContent();

        courseWareMapper.deleteByCourseId(course.getId());
        String key = RedisKey.ll_course_ware + course.getId();
        redisUtil.del(key);

        courseImgMapper.deleteByCourseId(course.getId());
        redisUtil.del(RedisKey.course_img + course.getId());
        if(course.getIsRelay() == 1){
            course.setSetRelayTime(new Date());
        }
        courseMapper.uploadCourse(course);
        if(1!=course.getIsOpened() && course.getIsRelay()==1){
            courseMapper.updateIsOpenById(1,course.getId());
        }

        if ("1".equals(course.getIsRecorded())) {
            if (course.getVideoAddress() == null) {
                course.setVideoAddress("");
            }
            //录播课更新video取消
            //videoService.updateVideoAddress(course.getId(), course.getVideoAddress().trim());
        }


        if (!StringUtils.isEmpty(photos)) {
            String[] ps = photos.split(";");
            List list = Arrays.asList(ps);
            courseWareMapper.insertList(list, course.getId());
        }
        StringBuffer contents = new StringBuffer();
        if (!StringUtils.isEmpty(imgs)) {
            String[] ps = imgs.split(";");
            List list = Arrays.asList(ps);
            courseImgMapper.insertList(list, course.getId());
        } else if (!StringUtils.isEmpty(imgContent)) {
            //courseContent=[{"img":"http://www.baidu.com" , "content":"dafdfa"},{"img":"http://www.baidu.com" , "content":"dafdfa"}]
            List<HashMap> list = JsonUtil.getList(imgContent, HashMap.class);
            List<Map<Long, String>> strListMap = new ArrayList<Map<Long, String>>();
            for (int i = 0; i < list.size(); i++) {
                Map m = list.get(i);
                String image = (String) m.get("img");
                String content = (String) m.get("content");
                String localId = (String) m.get("localId");
                if (("null".equals(image))) {
                    image = "";
                }
                if (StringUtils.isEmpty(image) && StringUtils.isEmpty(content)) {
                    continue;
                }
                CourseImg img = new CourseImg();
                img.setCourseId(course.getId());
                img.setContent(content);
                img.setAddress(image);
                img.setOrderSort(i);
                img.setCreateTime(new Date());
                img.setStatus("0");
                img.setLocalIds(localId);
                courseImgMapper.insert(img);
                if (!image.startsWith("http")) {
                    Map<Long, String> map = new HashMap<Long, String>();
                    map.put(img.getId(), image);
                    strListMap.add(map);
                }
                contents.append(content);
            }
            setClassImgWait2db(course.getId(), strListMap);
        }

        //如果是系列课里面的单节课，则发送更新系列课时间队列
        Course c = courseMapper.getCourse(course.getId());
        UpdateSeriesCourseTimeInterceptor.setSeriesId(c.getSeriesCourseId());
        redisUtil.del(RedisKey.ll_course_share_title + course.getId());
        //处理竖屏高斯模糊图
        Map map = new HashMap();
        map.put("courseId", course.getId());
        map.put("image", course.getVerticalCoverssAddress());
        redisUtil.lpush(RedisKey.ll_set_vertical_coverss_img, JsonUtil.toJson(map));

        //删除课程缓存
        String courseKey = RedisKey.ll_course + course.getId();
        redisUtil.del(courseKey);
        //更新课程名称
        Map map2 = new HashMap();
        map2.put("id", String.valueOf(course.getId()));
        map2.put("name", course.getLiveTopic());
        redisUtil.lpush(RedisKey.ll_update_course_name, JsonUtil.toJson(map2));

        //处理录播发送签别消息
        this.dealRecordInfo(course, contents.toString());
        if ("1".equals(course.getIsRecorded())) {
            //让录播课待审核中
            courseAuditService.updateGarbageStatusAndStatus(course.getId(), "", "0", "0");
        }
        //处理课程邀请卡
        if(!StringUtils.isEmpty(course.getModelUrl())) {
            addOrUpCourseCard(course, request);
        }
    }


    @Override
    public void updateSeriesCourse(CourseDto course, HttpServletRequest request) throws Exception {
        //课程封面
//        course.setCoverssAddress(picUtil.base64ToUrl(course.getCoverssAddress()));
        //简介图片
        String imgs = course.getCourseImg();

        String imgContent = course.getCourseContent();

        courseImgMapper.deleteByCourseId(course.getId());
        redisUtil.del(RedisKey.course_img + course.getId());
        courseMapper.uploadSeriesCourse(course);
        if(1!=course.getIsOpened() && course.getIsRelay()==1){
            courseMapper.updateIsOpenById(1,course.getId());
        }
        redisUtil.del(RedisKey.ll_course_share_title + course.getId());
        StringBuffer contents = new StringBuffer();
        if (!StringUtils.isEmpty(imgs)) {
            String[] ps = imgs.split(";");
            List list = Arrays.asList(ps);
            courseImgMapper.insertList(list, course.getId());
        } else if (!StringUtils.isEmpty(imgContent)) {
            //courseContent=[{"img":"http://www.baidu.com" , "content":"dafdfa"},{"img":"http://www.baidu.com" , "content":"dafdfa"}]
            List<HashMap> list = JsonUtil.getList(imgContent, HashMap.class);
            List<Map<Long, String>> strListMap = new ArrayList<Map<Long, String>>();
            for (int i = 0; i < list.size(); i++) {
                Map m = list.get(i);
                String image = (String) m.get("img");
                String content = (String) m.get("content");
                String localId = (String) m.get("localId");
                if (("null".equals(image))) {
                    image = "";
                }
                if (StringUtils.isEmpty(image) && StringUtils.isEmpty(content)) {
                    continue;
                }
                CourseImg img = new CourseImg();
                img.setCourseId(course.getId());
                img.setAddress(image);
                img.setContent(content);
                img.setOrderSort(i);
                img.setCreateTime(new Date());
                img.setStatus("0");
                img.setLocalIds(localId);
                courseImgMapper.insert(img);
                if (!image.startsWith("http")) {
                    Map<Long, String> map = new HashMap<Long, String>();
                    map.put(img.getId(), image);
                    strListMap.add(map);
                }
                contents.append(content);
            }
            setClassImgWait2db(course.getId(), strListMap);
        }

        String courseKey = RedisKey.ll_course + course.getId();
        redisUtil.del(courseKey);
        this.dealRecordInfo(course, contents.toString());
        if ("1".equals(course.getIsRecorded())) {
            courseAuditService.updateGarbageStatusAndStatus(course.getId(), "", "0", "0");
        }
        //处理系列课邀请卡
        if(!StringUtils.isEmpty(course.getModelUrl())){
            addOrUpCourseCard(course, request);
        }
    }

    /**
     * 单课或系列课邀请卡处理
     * @param course
     * @param request
     */
    public void addOrUpCourseCard(CourseDto course,HttpServletRequest request){
        //处理课程邀请卡
        CourseCard card = coursePrivateCardMapper.findCardUrlByCourseId(course.getId());
        String modelUrl = course.getModelUrl();
        String cardUrl = course.getCardUrl();
        Long courseId = course.getId();
        if(card == null){
            card = new CourseCard();
            card.setCourseId(courseId);
            card.setModelUrl(modelUrl);
            if(StringUtils.isEmpty(cardUrl)){
                String url = appUserService.doInvitationPrivateCard(modelUrl, request, courseId, "3");
                card.setCardUrl(url);
            }
            coursePrivateCardMapper.insertCourseCard(card);
        } else {
            if(StringUtils.isEmpty(card.getModelUrl()) || !StringUtils.isEmpty(modelUrl)){
                String url = appUserService.doInvitationPrivateCard(modelUrl, request, courseId, "3");
                card.setCardUrl(url);
            }
            card.setModelUrl(modelUrl);
            card.setCourseId(courseId);
            coursePrivateCardMapper.updateCourseCard(card);
        }
    }
    /**
     * 编辑课程图文混排
     *
     * @param courseId
     */
    public void setClassImgWait2db(long courseId, List<Map<Long, String>> serviceIds) {
        Map map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("serviceIdList", JsonUtil.toJson(serviceIds));
        redisUtil.lpush(RedisKey.ll_class_img_wait2db, JsonUtil.toJson(map));
    }
}
