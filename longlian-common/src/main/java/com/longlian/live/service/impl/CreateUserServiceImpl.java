package com.longlian.live.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.exception.GlobalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.AppUserCommonMapper;
import com.longlian.live.service.CreateUserService;
import com.longlian.live.util.yunxin.YunxinUserUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by han on 2017/7/9.
 */
@Service("createUserService")
public class CreateUserServiceImpl implements CreateUserService {
    private static Logger log = LoggerFactory.getLogger(CreateUserServiceImpl.class);

    @Autowired
    YunxinUserUtil yunxinUserUtil;
    @Autowired
    AppUserCommonMapper appUserMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private RedisLock redisLock;

    @Override
    public String createYunxinUser(Long accid, String name, String photo) {
        //200毫秒轮讯一次 ， 2秒算超时
       boolean flag = redisLock.lock(RedisKey.ll_lock_pre + accid , 200, 5);
       //获取锁失败，请等侍http://blog.csdn.net/u010359884/article/details/50310387
       if (!flag) {
           log.info("获取锁{}失败，请等侍!", RedisKey.ll_lock_pre + accid);
           GlobalExceptionHandler.sendEmail("获取锁"+ RedisKey.ll_lock_pre + accid +"失败，请等侍!" ,  "注意");
           return null;
       }
        try {
            String token = yunxinUserUtil.createUser(String.valueOf(accid), name, photo);
            if(!StringUtils.isEmpty(token)) {
                appUserMapper.updateYunXinToken(accid, token);
                //如果登录用户
                if (redisUtil.exists(RedisKey.ll_live_weixin_login_prefix + accid)) {
                    redisUtil.hset(RedisKey.ll_live_weixin_login_prefix + accid, "yunxinToken" , token );
                }
            }
            return token;
        }finally {
            redisLock.unlock(RedisKey.ll_lock_pre + accid);
        }
    }
}
