package com.longlian.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.service.ConsoleUserService;
@Service("consoleUserServiceImple")
public class ConsoleUserServiceImple implements ConsoleUserService {
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public String getUserRes(long userId, String key) {
        List<String> result = redisUtil.hmget(RedisKey.user_manage_login_prefix + userId, key);
        if (result != null && result.size() > 0 ) {
            return result.get(0);
        }
        return null;
    }
    
}
