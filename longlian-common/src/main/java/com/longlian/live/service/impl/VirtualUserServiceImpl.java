package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseAvatarUserService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.service.VirtualUserService;
import com.longlian.live.service.VisitCourseRecordService;
import com.longlian.live.util.yunxin.YunxinUserUtil;
import com.longlian.model.Course;
import com.longlian.model.StudyRecord;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.JoinCourseRecordType;
import com.longlian.type.ThirdPayDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 虚拟用户增加
 * Created by liuhan on 2017-07-13.
 */
@Service("virtualUserService")
public class VirtualUserServiceImpl implements VirtualUserService {
    @Autowired
    YunxinUserUtil yunxinUserUtil;
    @Autowired
    CourseAvatarUserService courseAvatarUserService;
    @Autowired
    VisitCourseRecordService visitCourseRecordService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    RedisUtil redisUtil;

    private Logger logg = LoggerFactory.getLogger(VirtualUserServiceImpl.class);
    @Override
    public Map addVirtualUser(Course course, String[] accids) throws Exception {
        Map result = yunxinUserUtil.addRobot(String.valueOf(course.getChatRoomId()) , accids);
        if (result == null || result.get("success" ) == null) {
            return result;
        }

        Object[] success = (Object[]) result.get("success");
        String[] s =  getString(success);
        logg.info("课程：{},ID:{},成功添加{}个虚拟用户：{}" ,course.getLiveTopic() ,  course.getId() , s.length , s);
        courseAvatarUserService.batchInsertUsers(course.getId() , s);

        for (String userId : s) {
            AppUserIdentity ai = new AppUserIdentity();
            ai.setId(Long.parseLong(userId));
            ThirdPayDto dto = new ThirdPayDto();
            dto.setAmount(new BigDecimal(0));
            dto.setCourseId(course.getId());
            dto.setInvitationAppId(0l);
            visitCourseRecordService.insertRecord(Long.parseLong(userId) , course.getId() , 0l, "0" ,course.getSeriesCourseId() , "0");

            if (course.getSeriesCourseId() > 0 ) {
                dto.setCourseId(course.getSeriesCourseId());
                joinCourseRecordService.handlerJoinCourseRecord( ai , dto , new ActResultDto() , JoinCourseRecordType.virtual_user.getValue()  , course.getRoomId());
            } else {
                joinCourseRecordService.handlerJoinCourseRecord( ai , dto , new ActResultDto() , JoinCourseRecordType.virtual_user.getValue()  , course.getRoomId());
            }

            //学习记录
            StudyRecord studyRecord = new StudyRecord();
            studyRecord.setAppId(Long.parseLong(userId));
            studyRecord.setCourseId( course.getId() );
            studyRecord.setCreateTime(new Date());
            studyRecord.setVirtualUser(true);
            studyRecord.setSeriesCourseId(course.getSeriesCourseId());
            redisUtil.lpush(RedisKey.ll_study_record_wait2db, JsonUtil.toJsonString(studyRecord));
        }

        return result;
    }

    public String[] getString(Object[] ss) {
        String[] s = new String[ss.length];
        for (int i = 0 ; i < ss.length ;i++) {
            s[i] = (String)ss[i];
        }
        return s;
    }
}
