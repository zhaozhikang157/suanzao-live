package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.InviCodeItemService;
import com.longlian.live.service.InviCodeService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.model.Course;
import com.longlian.model.InviCodeItem;
import com.longlian.mq.service.CourseService;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.JoinCourseRecordType;
import com.longlian.type.ThirdPayDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by admin on 2017/8/31.
 */
@Service
public class UseInviCodeProcess extends LongLianProcess{
    private static Logger log = LoggerFactory.getLogger(UseInviCodeProcess.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    InviCodeItemService itemService;
    @Autowired
    InviCodeService inviCodeService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;


    @Autowired
    CourseService courseService;
    private int threadCount = 10;

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil, RedisKey.ll_get_use_invi_code_course_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    private class GetData extends DataRunner {
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg){
            Map map = JsonUtil.getObject(msg, Map.class);
            String itemId = map.get("itemId").toString();
            String appId = map.get("appId").toString();
            String courseId = map.get("courseId").toString();
            //修改item的用户人
            InviCodeItem inviCodeItem = itemService.getItemInfo(Long.parseLong(itemId));
            if(inviCodeItem != null){
                Long useId = inviCodeItem.getUseAppId();
                if(useId < 1 ){
                    AppUserIdentity appUserIdentity = new AppUserIdentity();
                    appUserIdentity.setId(Long.parseLong(appId));
                    ThirdPayDto thirdPayDto = new ThirdPayDto();
                    thirdPayDto.setCourseId(Long.parseLong(courseId));
                    Course course = courseService.getCourse(thirdPayDto.getCourseId());
                    thirdPayDto.setAmount(course.getChargeAmt());
                    thirdPayDto.setInvitationAppId(0l);
                    ActResultDto resultDto = new  ActResultDto();
                    //插入购买记录
                    try {
                        String isUse = inviCodeService.isUseTime(inviCodeItem.getInviCodeId()+"" , Long.parseLong(appId));
                        if(isUse.equals("0")){
                            itemService.updateUseAppId(Long.valueOf(appId), Long.valueOf(itemId), new Date() , inviCodeItem.getInviCodeId());
                            joinCourseRecordService.handlerJoinCourseRecord(appUserIdentity, thirdPayDto, resultDto, JoinCourseRecordType.invi_code.getValue(), course.getRoomId());
                            redisUtil.del(RedisKey.ll_invi_code_use_time + inviCodeItem.getInviCodeId() + Long.parseLong(appId));
                        } else {
                            itemService.updateUseAppId(Long.valueOf(appId), Long.valueOf(itemId), null, inviCodeItem.getInviCodeId());
                        }
                    }catch (Exception e){
                        log.error(e.getMessage());
                    }

                }
            }
        }
    }
}
