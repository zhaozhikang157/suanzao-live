package com.longlian.console.timer;

import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.live.service.LiveRoomService;
import com.longlian.console.service.LongLianRewardService;
import com.longlian.live.service.FollowRewardRuleService;
import com.longlian.live.service.RewardRecordService;
import com.longlian.live.service.UserFollowService;
import com.longlian.model.FollowRewardRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/23.
 * 循环处理 推荐的老师奖励
 */
@Component("teachFollowRewardTimer")
public class TeachFollowRewardTimer extends AbstractShardingTask{

    private static Logger log = LoggerFactory.getLogger(TeachFollowRewardTimer.class);

    @Autowired
    LiveRoomService  liveRoomService;
    @Autowired
    LongLianRewardService longLianRewardService;

    @Autowired
    UserFollowService userFollowService;

    @Autowired
    RewardRecordService rewardRecordService;
    @Autowired
    FollowRewardRuleService followRewardRuleService;

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
        List<Map> list = liveRoomService.getAllUseRoom();//可以从slave走，
        for (Map map : list){
            long roomId = (long)map.get("id");
            long appId = (long)map.get("appId");
            long followCount = userFollowService.getCountByRoomId(roomId);//获取直播间关注数量
            //获取所有关注奖励规则记录
            List<FollowRewardRule> followRewardRuleList = followRewardRuleService.getFollowRewardRule4Redis();
            for(FollowRewardRule rewardRule :followRewardRuleList){
                if(rewardRule.getMenCount() > followCount) break;//人数不够跳出
                Set set =  rewardRecordService.getFollowRewardByTeachAppId(appId);
                if(set != null && set.contains(rewardRule.getId() + "")) continue;//已经发过奖励跳出
                //发放奖励
                longLianRewardService.teachFollowReward(appId, rewardRule.getId(), rewardRule.getAmount() ,rewardRule.getMenCount() + "");
            }
        }
    }*/
}
