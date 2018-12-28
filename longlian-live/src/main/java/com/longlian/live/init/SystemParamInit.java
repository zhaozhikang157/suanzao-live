package com.longlian.live.init;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.SystemParaService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 系统参数放入redis
 * Created by syl on 2016/6/15.
 */
@Component("systemParamInit")
public class SystemParamInit implements InitializingBean {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SystemParaService systemParaService;

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean isExistsKey =  redisUtil.exists(RedisKey.ll_live_system_param);//先取redis取
        if(!isExistsKey){
            //redisUtil.del(RedisKey.ll369_system_param);
            //如果没有则从数据库查询并存入数据库
            List<Map<String , String>> list = systemParaService.getAllList();
            for (Map<String , String> temp : list ){
                String value = temp.get("value");
                if("bank_out_remark".equals( temp.get("code").toString())){
                    value = temp.get("describe");
                }
                redisUtil.hset(RedisKey.ll_live_system_param , temp.get("code") , value);
            }
        }
    }
}
