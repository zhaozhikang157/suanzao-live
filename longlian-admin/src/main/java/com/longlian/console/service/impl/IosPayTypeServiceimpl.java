package com.longlian.console.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.IosPayTypeMapper;
import com.longlian.console.service.IosPayTypeService;
import com.longlian.model.IosPayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by admin on 2017/4/20.
 */
@Service("/iosPayTypeService")
public class IosPayTypeServiceimpl implements IosPayTypeService {
    private static Logger log = LoggerFactory.getLogger(IosPayTypeServiceimpl.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    IosPayTypeMapper iosPayTypeMapper;

    @Override
    public List<IosPayType> findIosPayTypeInfoPage(DataGridPage requestModel, IosPayType iosPayType) {
        return iosPayTypeMapper.findIosPayTypeInfoPage(requestModel, iosPayType);
    }

    @Override
    public IosPayType findById(long id) {
        return iosPayTypeMapper.findById(id);
    }

    @Override
    public void updateIosPayType(IosPayType iosPayType) {
        iosPayTypeMapper.updateIosPayType(iosPayType);
        depositRedis(iosPayType.getType());
    }

    @Override
    public void createIosPayType(IosPayType iosPayType) {
        iosPayTypeMapper.createIosPayType(iosPayType);
        depositRedis(iosPayType.getType());
    }

    @Override
    public void updateStatus(Long id, String status,String type) {
        iosPayTypeMapper.updateStatus(id, status);
        if("0".equals(type)|| "1".equals(type)){
            depositRedis(type);
        }

    }

    public void depositRedis(String type){
        if("0".equals(type)){   //IOS
            setRedis(RedisKey.ll_ios_pay_type, type);
        }else if("1".equals(type)){
            setRedis(RedisKey.android_pay_type,type);
        }
    }

    public void setRedis(String redisKey,String type){
        redisUtil.del(redisKey);
        List<IosPayType> list = iosPayTypeMapper.findAllPayInfo(type);
        String payInfo = JsonUtil.toJsonString(list);
        redisUtil.set(redisKey, payInfo);
    }
}
