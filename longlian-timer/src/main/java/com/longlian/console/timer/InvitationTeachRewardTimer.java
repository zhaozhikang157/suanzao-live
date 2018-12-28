package com.longlian.console.timer;

import com.huaxin.util.Utility;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.AppUserService;
import com.longlian.console.service.CourseService;
import com.longlian.console.service.LongLianRewardService;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23.
 * 循环处理 推荐的老师奖励
 */
@Component("invitationTeachRewardTimer")
public class InvitationTeachRewardTimer extends AbstractShardingTask{

    private static Logger log = LoggerFactory.getLogger(InvitationTeachRewardTimer.class);

    @Autowired
    AppUserService appUserService;
    @Autowired
    LongLianRewardService longLianRewardService;

    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;

    /**
     * 任务名称
     * @return
     */
    @Override
    public String getTaskName() {
        return "循环处理 推荐的老师奖励";
    }

    /**
     * 执行任务
     */
    @Override
    public void doExecute() {
        try {
            //job();
        } catch (Exception e) {
            log.error("循环处理 推荐的老师奖励异常："+e.getMessage());
            e.printStackTrace();
        }
    }

    /*public void job() throws  Exception {
        List<Map> list = appUserService.getInvitationTeachRewardList();//可以从slave走，
        String teach_course_reward_men_count_isfree = systemParaRedisUtil.getTeachCourseRewardMenCountIsFree();//老师讲课奖励（不免费、所有的） 0-所有的 1-收费的且成功的
        for (Map map : list){
            long roomId = (long)map.get("roomId");
            long appId = (long)map.get("appId");
            long invitationAppId = (long)map.get("invitationAppId");

            //返钱
            int payUserCount = joinCourseRecordService.getFirstPaySuccessRecordCount(roomId,teach_course_reward_men_count_isfree );
            longLianRewardService.invitationTeachReward( invitationAppId , appId , payUserCount);
        }
    }*/
}
