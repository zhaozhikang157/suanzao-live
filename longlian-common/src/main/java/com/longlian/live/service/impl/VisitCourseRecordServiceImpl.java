package com.longlian.live.service.impl;

import com.github.pagehelper.StringUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.weixin.type.WechatShareType;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.VisitCourseRecordMapper;
import com.longlian.live.service.VisitCourseRecordService;
import com.longlian.model.VisitCourseRecord;
import com.longlian.type.ReturnMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/2/24.
 */
@Service("visitCourseRecordService")
public class VisitCourseRecordServiceImpl implements VisitCourseRecordService {
    private static Logger log = LoggerFactory.getLogger(VisitCourseRecordServiceImpl.class);


    @Autowired
    VisitCourseRecordMapper visitCourseRecordMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisLock redisLock;

    @Override
    public ActResultDto insertRecord(Long appId, Long courseId,Long inviAppId , String type) {
        ActResultDto resultDto =  insertRecord(  appId,   courseId,  inviAppId ,   type  ,   0l , "0");
        return resultDto;

    }



    @Override
    public ActResultDto insertRecord(Long appId, Long courseId,Long inviAppId , String type  , Long seriesid, String wechatShareType) {
        ActResultDto resultDto = new ActResultDto();
        if(courseId == null || courseId == 0){
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            return resultDto;
        }
        VisitCourseRecord visitCourseRecord = new VisitCourseRecord();
        visitCourseRecord.setAppId(appId);
        visitCourseRecord.setCreateTime(new Date());
        visitCourseRecord.setCourseId(courseId);
        visitCourseRecord.setStatus(0);
        visitCourseRecord.setFromType(type);
        visitCourseRecord.setInvitationUserId(inviAppId);
        if (seriesid == null)
            seriesid = 0l;
        visitCourseRecord.setSeriesCourseId(seriesid);
        if (StringUtil.isEmpty(wechatShareType)) {
            wechatShareType = "0";
        }
        if (WechatShareType.circle_of_friend.getValue().equals(wechatShareType)) {
            wechatShareType = "1";
        }
        if (WechatShareType.friend.getValue().equals(wechatShareType)) {
            wechatShareType = "2";
        }
        visitCourseRecord.setFromShareType(wechatShareType);
        redisUtil.lpush(RedisKey.ll_live_visit_course_record_wait2db, JsonUtil.toJson(visitCourseRecord));
        log.info("存入mq:访问记录:"+JsonUtil.toJson(visitCourseRecord));
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;

    }



    @Override
    public void insertRecord(VisitCourseRecord visitCourseRecord) {
        visitCourseRecordMapper.insertRecord(visitCourseRecord);
    }

    /**
     * 是否已经存在
     * @param userId
     * @param courseId
     * @return
     */
    @Override
    public boolean isExist(Long userId , Long courseId){
        long  i =  visitCourseRecordMapper.findByUserIdAndCourseId(  userId ,   courseId);
        if (i > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 删除,返回影响的行数
     * @param userId
     * @param courseId
     * @return
     */
    @Override
    public int deleteRecord(Long userId, Long courseId){
        return  visitCourseRecordMapper.deleteRecord( userId ,   courseId);
    }

    /**
     * 将历史记录加载到内在里
     * @param courseId
     */
    @Override
    public void loadVisitRecord2Redis(long courseId) {
        String key = RedisKey.ll_cache_visit_record_record + courseId;

        if (redisUtil.exists(key)) {
            return ;
        }

        List<VisitCourseRecord> list = visitCourseRecordMapper.findByCourseId(courseId);
        for (VisitCourseRecord r : list) {
            redisUtil.sadd(key , String.valueOf(r.getAppId()));
        }
        redisUtil.expire(key , 60 * 60 * 24 *3);
    }
}
