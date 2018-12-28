package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.db.DataSource;
import com.huaxin.util.db.DynamicDataSourceKey;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.ChatRoomMsgMapper;
import com.longlian.live.dao.MsgCancelMapper;
import com.longlian.live.service.MsgCancelService;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;
import com.longlian.model.MsgCancel;
import com.longlian.type.ReturnMessageType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by admin on 2017/12/5.
 */
@Service("msgCancelService")
public class MsgCancelServiceImpl implements MsgCancelService {
    private static Logger log = LoggerFactory.getLogger(MsgCancelServiceImpl.class);

    @Autowired
    MsgCancelMapper msgCancelMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisLock redisLock;
    @Autowired
    ChatRoomMsgMapper chatRoomMsgMapper;


    @Override
    @DataSource(value = DynamicDataSourceKey.DS_LOG)
    public ActResultDto insertMsgCancel(MsgCancel msgCancel,Course course) {
        ActResultDto resultDto = new ActResultDto();
        boolean flag = redisLock.haslock(RedisKey.ll_msg_cancel_locak + msgCancel.getCourseId() + msgCancel.getMsgClientId(), 200 * 1000, 6);
        if(!flag){
            resultDto.setCode(ReturnMessageType.CREATE_COURSE_MANAGER_ING.getCode());
            resultDto.setMessage(ReturnMessageType.CREATE_COURSE_MANAGER_ING.getMessage());
            return resultDto;
        }
        try {
            if(course == null){
                resultDto.setCode(ReturnMessageType.NO_COURSE.getCode());
                resultDto.setMessage(ReturnMessageType.NO_COURSE.getMessage());
                return resultDto;
            }
            if(!course.getChatRoomId().toString().equals(msgCancel.getChatRoomId()+"")){
                resultDto.setCode(ReturnMessageType.NO_COURSE.getCode());
                resultDto.setMessage(ReturnMessageType.NO_COURSE.getMessage());
                return resultDto;
            }
            if("1".equals(course.getIsDelete()) || "1".equals(course.getStatus())){
                resultDto.setCode(ReturnMessageType.COURSE_DELETE.getCode());
                resultDto.setMessage(ReturnMessageType.COURSE_DELETE.getMessage());
                return resultDto;
            }
            if(course.getEndTime() != null){
                resultDto.setCode(ReturnMessageType.LIVE_COURSE_IS_END.getCode());
                resultDto.setMessage(ReturnMessageType.LIVE_COURSE_IS_END.getMessage());
                return resultDto;
            }
            if(!(course.getAppId()+"").equals(msgCancel.getOptId())){
                resultDto.setCode(ReturnMessageType.ONLY_TEACHER_OPTION.getCode());
                resultDto.setMessage(ReturnMessageType.ONLY_TEACHER_OPTION.getMessage());
            }
            redisUtil.lpush(RedisKey.ll_msg_cancel, JsonUtil.toJson(msgCancel));
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        }catch (Exception e){
            log.info("撤回消息失败,e=" + e + " :撤回消息:" + JsonUtil.toJson(msgCancel));
            resultDto.setCode(ReturnMessageType.Msg_CANCEL_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.Msg_CANCEL_ERROR.getMessage());
        }finally {
            redisLock.unlock(RedisKey.ll_msg_cancel_locak + msgCancel.getCourseId() + msgCancel.getMsgClientId());
            return resultDto;
        }
    }

    @Override
    @DataSource(value = DynamicDataSourceKey.DS_LOG)
    public void insertMsg(MsgCancel msgCancel) {
        msgCancelMapper.insertMsgCancel(msgCancel);
        redisUtil.del(RedisKey.ll_course_msg_cancel + msgCancel.getCourseId());
    }

    /**
     * 查询该课程的用户禁言人员
     * @param courseId
     * @return
     */
    @Override
    @DataSource(value = DynamicDataSourceKey.DS_LOG)
    public String findMsgCancel(Long courseId) {
        String redisValue = redisUtil.get(RedisKey.ll_course_msg_cancel + courseId);
        if(StringUtils.isNotEmpty(redisValue)){
            return redisValue;
        }
        Set<String> userIdSet = msgCancelMapper.findMsgCancel(courseId);
        StringBuffer str = new StringBuffer();
        for(String userId : userIdSet){
            str.append(userId);
            str.append(",");
        }
        if (str.length() > 0) {
            redisUtil.set(RedisKey.ll_course_msg_cancel + courseId , str.toString());
            redisUtil.expire(RedisKey.ll_course_msg_cancel + courseId , 3 * 24 * 60 * 60);
            return str.substring(0, str.length() - 1);
        }
        return null;
    }
}
