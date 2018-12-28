package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.ShareRecordDto;
import com.longlian.live.dao.ShareRecordMapper;
import com.longlian.live.service.*;
import com.longlian.model.AppUser;
import com.longlian.model.Course;
import com.longlian.model.LiveRoom;
import com.longlian.model.ShareRecord;
import com.longlian.type.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2017/2/24.
 */
@Service("shareRecordService")
public class ShareRecordServiceimpl implements ShareRecordService {
    private static Logger log = LoggerFactory.getLogger(ShareRecordServiceimpl.class);
    @Autowired
    SendMsgService sendMsgService;
    @Autowired
    ShareRecordMapper shareRecordMapper;
    @Autowired
    CourseService courseService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    AppUserService appUserService;

    @Override
    public ActResultDto insertShare(ShareRecordDto shareRecord) {
        ActResultDto resultDto = new ActResultDto();
        log.info("---------------------shareRecordServiceImpl-----------------");
        try {
            if(0 == shareRecord.getCourseId()){
                int i = shareRecordMapper.findAppIdAndRoomId(shareRecord.getRoomId(), shareRecord.getAppId());
                log.info("i="+i);
                if(i == 0){
                    shareRecordMapper.insertShareRecord(shareRecord);
                    log.info("insertSuccess");
                    //sendMag(shareRecord);
                }
            }else if(0 == shareRecord.getRoomId()){
                int i = shareRecordMapper.findAppIdAndCourseId(shareRecord.getCourseId(), shareRecord.getAppId());
                log.info("i="+i+"findAppIdAndCourseId");
                if(i == 0){
                    shareRecordMapper.insertShareRecord(shareRecord);
                    log.info("insertSuccess");
                    // sendMag(shareRecord);
                }
            }
            if(!Utility.isNullorEmpty(shareRecord.getImgUrl()) &&
                    !Utility.isNullorEmpty(shareRecord.getAppId()) &&
                    ("LIVE_ROOM_INVI_CARD".equals(shareRecord.getSystemType()) || "COURSE_INVI_CARD".equals(shareRecord.getSystemType()) )
                    ){
                //查下用户的openid，是关注的用户才发，后面做扩展吧
                AppUser appUser = appUserService.getById(shareRecord.getAppId());
                if(appUser != null ){
                    shareRecord.setOpenid(appUser.getOpenid());
                    sendRoomOrCourseCardWachatMessage(shareRecord);
                }
            }
            log.info("shareRecord="+shareRecord.getClass());
        }catch (Exception e){
            log.info("e="+e);
            e.printStackTrace();
        }
        return resultDto;
    }

    public void sendMag(ShareRecord shareRecord){
        Long courseId = shareRecord.getCourseId();
        if (courseId != 0) {
            Course course = courseService.getCourse(courseId);
            sendMsgService.sendMsg(course.getAppId(), MsgType.SHARE_REMIND.getType(), courseId
                    , MsgConst.replace(MsgConst.SHARE_COURSE_REMIND_TEACHER_CONTENT, course.getLiveTopic())
                    , "");
        } else {
            LiveRoom liveRoom = liveRoomService.findById(shareRecord.getRoomId());
            sendMsgService.sendMsg(liveRoom.getAppId(), MsgType.SHARE_REMIND.getType(), shareRecord.getId()
                    , MsgConst.SHARE_ROOM_REMIND_TEACHER_CONTENT
                    , "");
        }
    }


    /**
     * 发送分享直播间和课程邀请卡发送微信消息
     * @param shareRecord
     */
    public void sendRoomOrCourseCardWachatMessage(ShareRecordDto shareRecord){
        redisUtil.lpush(RedisKey.ll_live_share_course_or_room_card_send_wechat_messsage , JsonUtil.toJsonString(shareRecord));
    }

}
