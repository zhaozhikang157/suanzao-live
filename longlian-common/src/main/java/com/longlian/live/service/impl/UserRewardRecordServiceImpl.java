package com.longlian.live.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.constant.DateConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.UserRewardRecordMapper;
import com.longlian.live.service.UserRewardRecordService;
import com.longlian.model.UserRewardRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Tuple;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2017/5/23.
 */
@Service("userRewardRecordService")
public class UserRewardRecordServiceImpl implements UserRewardRecordService {
    @Autowired
    private RedisLock redisLock;
    @Autowired
    UserRewardRecordMapper userRewardRecordMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void insert(UserRewardRecord rewardRecord) {
        rewardRecord.setCreateTime(new Date());
        if(rewardRecord.getRewardCount() == null) rewardRecord.setRewardCount(1);
        if(rewardRecord.getType() == null) rewardRecord.setType("0");
        userRewardRecordMapper.insert(rewardRecord);
    }

    /**
     * 单节课打赏排行榜
     * @param dg
     * @param courseId
     * @return
     */
    @Override
    public List<Map> selectUserRewardSort(DataGridPage dg, long courseId) {
        List<Map> list = userRewardRecordMapper.selectUserRewardSortPage(dg, courseId);
        if(list!=null && list.size()>0){
            for (Map map : list) {
                map.put("amount", map.get("amount").toString());
            }
        }
        return list;
    }

    @Override
    public List<Map> findRewInfoPage(long appId, DataGridPage page) {
        List<Map> userReward = userRewardRecordMapper.findRewInfoPage(appId, page);
        for(Map map : userReward){
            Long courseId = Long.valueOf(map.get("courseId").toString());
            List<String> courseCount = userRewardRecordMapper.findRewCountbyAppId(courseId);
            if(courseCount!=null && courseCount.size()>0){
                map.put("courseCount",courseCount.size());
            }else{
                map.put("courseCount",0);
            }
        }
        return userReward;
    }

    @Override
    public List<Map> findTodayRewInfoPage(long appId, DataGridPage page) {
        List<Map> userReward = userRewardRecordMapper.findTodayRewInfoPage(appId, page);
        for(Map map : userReward){
            Long courseId = Long.valueOf(map.get("courseId").toString());
            int count = userRewardRecordMapper.findTodayRewCountbyAppId(courseId);
            map.put("courseCount",count);
        }
        return userReward;
    }

    @Override
    public List<Map> findRewInfoPageByCourseIdPage(long appId, DataGridPage page, long courseId) {
        List<Map> list = userRewardRecordMapper.findRewInfoPageByCourseIdPage(appId, page, courseId);
        for(Map map : list){
            if(map.get("createTime")!=null ){
                map.put("createTime",map.get("createTime").toString().substring(0,map.get("createTime").toString().length()-2));
            }
        }
        return list;
    }
    private List<Map> getUserRewardFromRedis(String key ) {
        Set<Tuple> list =  redisUtil.zrangeWithScores(key , 0 , -1);
        List<Map> users = new ArrayList<>();

        for(Tuple tuple : list) {
            Map map = new HashMap();
            String id = tuple.getElement();
            map.put("userId" , id);
            map.put("amout" , new BigDecimal(String.valueOf(tuple.getScore())));
            users.add(map);
        }
        return users;
    }

    /**
     * 将打赏记录一次性加载到reids中
     * @param courseId
     * @param isLock
     */
    @Override
    public List<Map> loadUserReward2Redis(Long courseId, boolean isLock) {
        String key = RedisKey.ll_user_reward + courseId;
        if (redisUtil.exists(key)) {
            return getUserRewardFromRedis(key) ;
        }

        String lockKey = RedisKey.ll_load_user_reward_lock_pre + courseId;
        if (isLock) {
            redisLock.lock(lockKey , 200 * 1000, 5);
        }
        try {
            if (redisUtil.exists(key)) {
                return getUserRewardFromRedis(key) ;
            }
            List<Map> list = userRewardRecordMapper.statRewardCountbyAppId(courseId);
            List<Map> result = new ArrayList<>();
            for (Map map : list) {
                String userId = String.valueOf(map.get("userId"));
                BigDecimal amout = (BigDecimal) map.get("amout");
                redisUtil.zadd(key ,userId , amout.doubleValue());

                Map map2 = new HashMap();
                map2.put("userId" , userId);
                map2.put("amout" ,  amout);
                result.add(map2);
            }
            redisUtil.expire(key , DateConst.oneDay * 5);
            return result;
        } finally {
            if (isLock) {
                redisLock.unlock(lockKey);
            }
        }

    }

    /**
     * 增加打赏到用户里面
     * @param courseId
     * @param userId
     * @param rewardMoney
     * @return
     */
    @Override
    public BigDecimal addUserReward(Long courseId, Long userId, BigDecimal rewardMoney) {
        List<Map> list = loadUserReward2Redis(courseId, true);
        String key = RedisKey.ll_user_reward + courseId;
        Double score = redisUtil.zscore(key , String.valueOf(userId));
        // 如果list != null  , 说明是，刚查询出来的，如果list==null 说明，晚
        if (list != null && list.size() > 0 ) {
            //不等于null才加
            if (score != null) {
                rewardMoney = rewardMoney.add(new BigDecimal(String.valueOf(score)));
            }
            redisUtil.zadd(key ,  String.valueOf(userId) ,rewardMoney.doubleValue());
            redisUtil.expire(key , DateConst.oneDay * 5);
            return rewardMoney;
        } else {
            return new BigDecimal(score);
        }
    }

    /**
     * 获取 今日 课程收益
     * @param appId
     * @return
     */
    @Override
    public BigDecimal getTodayRewardIncome(Long appId) {
        BigDecimal courseIncome = userRewardRecordMapper.getTodayRewardIncome(appId);
        return courseIncome;
    }

    @Override
    public List<Map> findTodayRelayDetail(long id, DataGridPage dataGridPage) {
        return this.userRewardRecordMapper.findTodayRelayDetailPage(id, dataGridPage);
    }
}
