package com.longlian.live.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.UserRewardTypeMapper;
import com.longlian.live.service.UserRewardTypeService;
import com.longlian.model.AdvertisingDisplay;
import com.longlian.model.UserRewardType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhan on 2017-02-25.
 */
@Service("userRewardTypeService")
public class UserRewardTypeServiceImpl implements UserRewardTypeService {
    private static Logger log = LoggerFactory.getLogger(UserRewardTypeServiceImpl.class);
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserRewardTypeMapper userRewardTypeMapper;

    /**
     * 根据状态类型获取用户打赏类型
     * @param status
     * @return
     */
    @Override
    public List<UserRewardType> getUseList(String status) {
        List<UserRewardType> list = new ArrayList<UserRewardType>();
        List<UserRewardType> allList = getAllList();
        for (UserRewardType userRewardType : allList){
            if(status.equals(userRewardType.getStatus())){
                list.add(userRewardType);
            }
        }
        return list;
    }

    /**
     * 根据Id获取对象
     * @param id
     * @return
     */
    @Override
    public UserRewardType getById(long id) {
        List<UserRewardType> allList = getAllList();
        for (UserRewardType userRewardType1 : allList){
            if(id == userRewardType1.getId()){
                return  userRewardType1;
            }
        }
        return null;
    }

    @Override
    public List<UserRewardType> findrewardTypeInfoPage(DataGridPage page, UserRewardType userRewardType) {
        return userRewardTypeMapper.findrewardTypeInfoPage(page,userRewardType);
    }

    @Override
    public void createUserRewardType(UserRewardType userRewardType) {
        userRewardTypeMapper.createUserRewardType(userRewardType);
        redisUtil.del(RedisKey.ll_live_user_reward_type);
    }

    @Override
    public void updateUserRewardType(UserRewardType userRewardType) {
        userRewardTypeMapper.updateUserRewardType(userRewardType);
        redisUtil.del(RedisKey.ll_live_user_reward_type);
    }

    @Override
    public void updateStatus(long id, String status) {
        userRewardTypeMapper.updateStatus(id,status);
        redisUtil.del(RedisKey.ll_live_user_reward_type);
    }

    /**
     *获取所有的数据
     */
    public   List<UserRewardType> getAllList(){
        List<UserRewardType> list = new ArrayList<UserRewardType>();
        if(redisUtil.exists(RedisKey.ll_live_user_reward_type)){
            List<String> arg = redisUtil.lrangeall(RedisKey.ll_live_user_reward_type);//获取所有的
            if (arg == null) return list;
            for (String temp : arg) {
                if (!Utility.isNullorEmpty(temp)) {
                    UserRewardType userRewardType = JsonUtil.getObject(temp, UserRewardType.class);
                    list.add(userRewardType);
                }
            }
        }else{
            list =  userRewardTypeMapper.getUseList(null);//获取所有的
            //存入缓存
            List<String> redisList = new ArrayList<String>();
            for (UserRewardType userRewardType : list){
                redisList.add(JsonUtil.toJsonString(userRewardType));
            }
            redisUtil.rpushlist(RedisKey.ll_live_user_reward_type, redisList);
        }
        return  list;
    }
}

