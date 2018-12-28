package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.GagMapper;
import com.longlian.live.service.GagService;
import com.longlian.model.Gag;
import com.longlian.type.ReturnMessageType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

/**
 * Created by admin on 2017/8/7.
 */
@Service("/gayService")
public class GagServiceImpl implements GagService {
    private static Logger log = LoggerFactory.getLogger(GagServiceImpl.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    GagMapper gagMapper;


    /**
     * 设置用户禁言
     * @param userId
     * @param courseId
     * @return
     */
    @Override
    public ActResultDto setGag(Long userId, Long courseId , Long optId) {
        ActResultDto resultDto = new ActResultDto();
        Gag gag = new Gag();
        gag.setCourseId(courseId);
        gag.setCreateTime(new Date());
        gag.setOptId(optId);
        gag.setUserId(userId);
        redisUtil.lpush(RedisKey.ll_set_user_gag_wait2db, JsonUtil.toJson(gag));
        return resultDto;
    }

    /**
     * 取消用户禁言
     * @param userId
     * @param courseId
     * @return
     */
    @Override
    public ActResultDto delGag(Long userId, Long courseId) {
        ActResultDto resultDto = new ActResultDto();
        Gag gag = new Gag();
        gag.setCourseId(courseId);
        gag.setUserId(userId);
        redisUtil.lpush(RedisKey.ll_del_user_gag_wait2db, JsonUtil.toJson(gag));
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    /**
     * 设置用户禁言 mq
     * @param gag
     * @return
     */
    @Override
    public void setGagMq(Gag gag) {
        gagMapper.setGag(gag);
    }

    /**
     * 查询该课程的用户禁言人员
     * @param courseId
     * @return
     */
    @Override
    public String findUserIdByCourseId(Long courseId) {
        String redisValue = redisUtil.get(RedisKey.ll_set_user_gag + courseId);
        if(StringUtils.isNotEmpty(redisValue)){
            return redisValue;
        }
        Set<String> userIdSet = gagMapper.findUserIdByCourseId(courseId);
        StringBuffer str = new StringBuffer();
        for(String userId : userIdSet){
            str.append(userId);
            str.append(",");
        }
        if (str.length() > 0) {
            return str.substring(0, str.length() - 1);
        }
        return null;
    }

    /**
     * 查询该课程的禁言用户人员 是否有相同的
     * @param gag
     * @return
     */
    @Override
    public int findSameUserId(Gag gag) {
        return gagMapper.findSameUserId(gag);
    }

    /**
     * 取消用户禁言 mq
     * @param gag
     * @return
     */
    @Override
    public void delGagMq(Gag gag) {
        gagMapper.delGag(gag);
    }
}
