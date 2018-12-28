package com.longlian.live.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.newdao.LiveConnectMapper;
import com.longlian.live.newdao.LiveConnectRequestMapper;
import com.longlian.live.service.*;
import com.longlian.model.AppUser;
import com.longlian.model.Course;
import com.longlian.model.LiveConnect;
import com.longlian.type.ReturnMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Tuple;

import java.util.*;

@Service("liveConnectService")
public class LiveConnectServiceImpl implements LiveConnectService {
    //add log print
    private static Logger log = LoggerFactory.getLogger(LiveConnectServiceImpl.class);

    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    StudyRecordService studyRecordService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    AppUserService appUserService;
    @Autowired
    LiveConnectMapper liveConnectMapper;
    @Autowired
    LiveConnectRequestMapper liveConnectRequestMapper;
    @Autowired
    CourseService courseService;

    @Override
    public List<Map> getOnlineConnectList(Long courseId, Integer offset, Integer pageSize) {
        if (Utility.isNullorEmpty(pageSize) || 0 == pageSize) {
            pageSize = 10;
        }
        if (0 == offset) {
            pageSize = pageSize - 1;
        } else {
            pageSize = pageSize + offset - 1;
        }
        Map map = null;
        List<Map> list = new ArrayList<Map>();
        Map<String, String> redisMap = redisUtil.hmgetAll(RedisKey.course_user_client_type + courseId);
        Set mapSet = new HashSet();
        for (Map.Entry<String, String> entry : redisMap.entrySet()) {
            if ("1".equals(entry.getValue())) {
                mapSet.add(entry.getKey());
            }
        }
        Course course = courseBaseService.getCourseFromRedis(courseId);
        String key = null;
        //系列课里面的单节课
        if (course.getSeriesCourseId() > 0) {
            studyRecordService.loadCourseUser(courseId);
            key = RedisKey.ll_visit_course_user_key + courseId;
        } else {
            joinCourseRecordService.loadJoinCourseUser(courseId);
            key = RedisKey.ll_join_course_user_key + courseId;
        }
        Set<Tuple> tuples = redisUtil.zrevrangeWithScores(key, offset, pageSize);
        for (Tuple tuple : tuples) {
            String id = tuple.getElement();
            map = new HashMap();
            if (mapSet.contains(id)) {
                map.put("id", id);
                AppUser appUser = appUserService.getById(Long.parseLong(id));
                if (!Utility.isNullorEmpty(appUser)) {
                    map.put("name", appUser.getName());
                    map.put("photo", appUser.getPhoto());

                }
                map.put("isConnecting", 0);
                list.add(map);
            }
        }
        return list;
    }
    @Override
    public ActResultDto appLyLiveConnect(Long appId,LiveConnect liveConnect) throws Exception {
            ActResultDto resultDto = new ActResultDto();
        try {
//            Course course =  courseMapper.getCourse(liveConnect.getCourseId());
//            if(Utility.isNullorEmpty(course)){
//                resultDto.setCode(ReturnMessageType.NO_COURSE.getCode());
//                resultDto.setMessage(ReturnMessageType.NO_COURSE.getMessage());
//                return resultDto;
//            }
//            liveConnect.setApplyUser(appId);
//            liveConnect.setApplyTime(new Date());
//            liveConnect.setStatus("0");
//            liveConnect.setStudent(appId);
//            liveConnect.setTeacher(course.getAppId());
//            liveConnect.setApplyTime(new Date());
//            liveConnectMapper.insert(liveConnect);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultDto;
    }

    @Override
    public List<Map> getConnectApplyListPage(Long courseId, Integer offset, Integer pageSize) {
        return null;
    }

    @Override
    public List<Map> getLatelyConnectListPage(Long courseId, Integer offset, Integer pageSize) {
        return null;
    }



    @Override
    public void updateStatus(Long connectid, String flag) {

    }

    @Override
    public ActResultDto updateStatus(Long connectid, String flag, String erreMsg) {
        liveConnectRequestMapper.updateStatus(connectid,   flag , erreMsg);
        return ActResultDto.success();
    }

    @Override
    public ActResultDto updateStatusV2(Long courseId,Long applyUser, String flag, String erreMsg) {
        ActResultDto resultDto  = new ActResultDto();
        Course course = courseService.getCourseFromRedis(courseId);
        if(course == null){
            resultDto.setCode(ReturnMessageType.NO_COURSE.getCode());
            resultDto.setMessage(ReturnMessageType.NO_COURSE.getMessage());
            return resultDto;
        }
        if("1".equals(course.getStatus()) || "1".equals(course.getIsDelete())){
            resultDto.setCode(ReturnMessageType.COURSE_IS_DEL.getCode());
            resultDto.setMessage(ReturnMessageType.COURSE_IS_DEL.getMessage());
            return resultDto;
        }
        Long reqId = liveConnectMapper.findByCouseIdAndApplyUser(courseId,applyUser);
        if(reqId == null || reqId < 1){
            resultDto.setMessage(ReturnMessageType.UPDATE_CONNECT_ERROR.getMessage());
            resultDto.setCode(ReturnMessageType.UPDATE_CONNECT_ERROR.getCode());
            return resultDto;
        }
        int i = liveConnectRequestMapper.updateStatus(  reqId,   flag , erreMsg);
        if(i > 0){
            return resultDto;
        }else{
            resultDto.setMessage(ReturnMessageType.UPDATE_CONNECT_ERROR.getMessage());
            resultDto.setCode(ReturnMessageType.UPDATE_CONNECT_ERROR.getCode());
            return resultDto;
        }
    }

    @Override
    public LiveConnect getNowConnectByCourseId(Long courseId) {
        return null;
    }

    /**
     * WHA -  The student asked the teacher to connect to the network voice：Request list
     * @param teacher : Teacher ID
     * @param student : Student ID
     * @param courseId :Course ID
     * @param offset :offset
     * @param pageSize :pageSize
     * @return
     */
    @Override
    public ActResultDto getEvenForWheatListPage(Optional<Integer> offset, Optional<Integer> pageSize,  Optional<Long> teacher,  Optional<Long> student,  Optional<Long> courseId) {
        ActResultDto result = new ActResultDto();
        DataGridPage dg = new DataGridPage();//创建分页模型对象
        if(!offset.isPresent()){
            offset = Optional.of(0);
        }
        if(!pageSize.isPresent()){
            pageSize = Optional.of(10);
        }
        if(!teacher.isPresent()){
            log.info("EvenForWheatList--参数缺失：teacher");
            return result;
        }
        if(!student.isPresent()){
            log.info("EvenForWheatList--参数缺失：student");
            student = Optional.of(0L);
        }
        if(!courseId.isPresent()){
            log.info("EvenForWheatList--参数缺失：courseId");
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return result;
        }
        dg.setOffset(offset.get());//设置分页偏移量
        dg.setPageSize(pageSize.get());//设置分页每页数据量
        List<Map> evenForWheatListPage = liveConnectMapper.getEvenForWheatListPage(dg, teacher.get(), student.get(), courseId.get());
        if (evenForWheatListPage != null && evenForWheatListPage.size() > 0) {
            result.setData(evenForWheatListPage);
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        }else {
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            result.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return result;
    }

    /**
     * WHA -  The student asked the teacher to connect to the network voice：Request list
     * @param teacher : Teacher ID
     * @param student : Student ID
     * @param courseId :Course ID
     * @param offset :offset
     * @param pageSize :pageSize
     * @return
     */
    @Override
    public ActResultDto getEvenForWheatEndListPage(Optional<Integer> offset, Optional<Integer> pageSize,  Optional<Long> teacher,  Optional<Long> student,  Optional<Long> courseId) {
        ActResultDto result = new ActResultDto();
        DataGridPage dg = new DataGridPage();//创建分页模型对象
        if(!offset.isPresent()){
            offset = Optional.of(0);
        }
        if(!pageSize.isPresent()){
            pageSize = Optional.of(10);
        }
        if(!teacher.isPresent()){
            log.info("EvenForWheatList--参数缺失：teacher");
            return result;
        }
        if(!student.isPresent()){
            log.info("EvenForWheatList--参数缺失：student");
            student = Optional.of(0L);
        }
        if(!courseId.isPresent()){
            log.info("EvenForWheatList--参数缺失：courseId");
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return result;
        }
        dg.setOffset(offset.get());//设置分页偏移量
        dg.setPageSize(pageSize.get());//设置分页每页数据量
        List<Map> getEvenForWheatEndListPage = liveConnectMapper.getEvenForWheatEndListPage(dg, teacher.get(), student.get(), courseId.get());
        if (getEvenForWheatEndListPage != null && getEvenForWheatEndListPage.size() > 0) {
            result.setData(getEvenForWheatEndListPage);
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        }else {
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            result.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return result;
    }

    //    @Override
//        public List<Map> getConnectApplyListPage(Long courseId, Integer offset, Integer pageSize) {
//        DataGridPage dg = new DataGridPage();
//        dg.setOffset(offset);
//        dg.setPageSize(pageSize);
//        return liveConnectMapper.getConnectApplyListPage(courseId, dg);
//    }
//
//    @Override
//    public List<Map> getLatelyConnectListPage(Long courseId, Integer offset, Integer pageSize) {
//        DataGridPage dg = new DataGridPage();
//        dg.setOffset(offset);
//        dg.setPageSize(pageSize);
//        return liveConnectMapper.getLatelyConnectListPage(courseId, dg);
//    }
//
//    @Override
//    public void addLiveConnect(LiveConnect connect) {
//        liveConnectMapper.insert(connect);
//    }
//
//    @Override
//    public void updateStatus(Long connectid, String flag) {
//
//        liveConnectMapper.updateStatus(  connectid,   flag , "");
//    }
//
//    @Override
//    public void updateStatus(Long connectid, String flag, String erreMsg) {
//        liveConnectMapper.updateStatus(  connectid,   flag , erreMsg);
//    }
//
//    @Override
//    public LiveConnect getNowConnectByCourseId(Long courseId) {
//        return liveConnectMapper.getNowConnectByCourseId(  courseId);
//    }
}
