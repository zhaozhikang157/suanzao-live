package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.FollowRewardRuleMapper;
import com.longlian.live.service.FollowRewardRuleService;
import com.longlian.model.CourseType;
import com.longlian.model.FollowRewardRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pangchao on 2017/2/25.
 */
@Service("followRewardService")
public class FollowRewardRuleServiceImpl implements FollowRewardRuleService {
    private static Logger log = LoggerFactory.getLogger(FollowRewardRuleServiceImpl.class);
    @Autowired
    FollowRewardRuleMapper followRewardMapper;

    @Autowired
    RedisUtil redisUtil;
    @Override
    public List<FollowRewardRule> getListPage(DatagridRequestModel datagridRequestModel, FollowRewardRule followRewardRule) {
        return followRewardMapper.getListPage(datagridRequestModel, followRewardRule);
    }

    @Override
    public void deleteById(long id,long status) throws Exception {
        followRewardMapper.deleteById(id,status);
        resetRedisData();
    }

    @Override
    public FollowRewardRule findById(long id) {
        return followRewardMapper.findById(id);
    }

    @Override
    public void doSaveAndUpdate(FollowRewardRule followRewardRule) {
        if(followRewardRule.getId()>0){
            followRewardMapper.update(followRewardRule);
        }else{
            followRewardMapper.insert(followRewardRule);
        }
        resetRedisData();
    }

    /**
     * 从redis读取数据
     * @return
     */
    @Override
    public List getFollowRewardRule4Redis() {
        List<FollowRewardRule> list = new ArrayList<FollowRewardRule>();
        Boolean isExistsKey = redisUtil.exists(RedisKey.ll_live_follow_reward_rule);//先取redis取
        if (isExistsKey) {
            list = getObjList();
            return list;
        }
        return   resetRedisData();
    }



    /**
     * 将从redis取出的list 字符串 转对象
     *
     * @return
     */
    public List<FollowRewardRule> getObjList() {
        List<FollowRewardRule> list = new ArrayList<FollowRewardRule>();
        List<String> arg = redisUtil.lrangeall(RedisKey.ll_live_follow_reward_rule);
        if (arg == null) return list;
        for (String temp : arg) {
            if (!Utility.isNullorEmpty(temp)) {
                FollowRewardRule courseType = JsonUtil.getObject(temp, FollowRewardRule.class);
                list.add(courseType);
            }
        }
        return list;
    }

    /**
     * 重新设置redis 数据
     */
    public List<FollowRewardRule>  resetRedisData() {
        List<FollowRewardRule>  list =  followRewardMapper.getList();
        boolean isExistsKey = redisUtil.exists(RedisKey.ll_live_follow_reward_rule);//先去redis取
        if (isExistsKey) redisUtil.del(RedisKey.ll_live_follow_reward_rule);
        List<String> redisList = new ArrayList<String>();
        for (FollowRewardRule resource : list) {
            redisList.add(JsonUtil.toJson(resource));
        }
        redisUtil.rpushlist(RedisKey.ll_live_follow_reward_rule, redisList);
        return  list;
    }

}
