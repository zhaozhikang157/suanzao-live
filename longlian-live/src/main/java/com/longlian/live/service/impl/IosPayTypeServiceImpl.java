package com.longlian.live.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.IosPayTypeMapper;
import com.longlian.live.service.IosPayTypeService;
import com.longlian.model.IosPayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/19.
 */
@Service("/iosPayTypeService")
public class IosPayTypeServiceImpl implements IosPayTypeService {
    private static Logger log = LoggerFactory.getLogger(IosPayTypeServiceImpl.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    IosPayTypeMapper iosPayTypeMapper;

    /**
     * 根据类型获取响应的支付信息
     * @param type
     * @return
     */
    @Override
    public List<IosPayType> getIosPay(String type) {
        List<IosPayType> list = new ArrayList<IosPayType>();
        if(type.equals("0")){   //IOS
            list = getAllPayTypeByType(RedisKey.ll_ios_pay_type , type);
        }else if(type.equals("1")){ //android
            list = getAllPayTypeByType(RedisKey.android_pay_type , type);
        }
        return list;
    }

    /**
     * 根据类型获取响应的支付信息
     * @param redisKey
     * @param type
     * @return
     */
    public List<IosPayType> getAllPayTypeByType(String redisKey , String type){
        String redisIos = redisUtil.get(redisKey);
        List<IosPayType> list = null;
        if(!StringUtils.isEmpty(redisIos)){
            list  = JSONArray.parseArray(redisIos, IosPayType.class);

        }else{
            list = iosPayTypeMapper.findAllIosPayInfo(type);
            String ios = JsonUtil.toJsonString(list);
            redisUtil.set(redisKey,ios);
        }
        List<IosPayType> newList = new ArrayList<>();
        for(IosPayType iosPayType : list){
            if("0".equals(iosPayType.getStatus())){
                newList.add(iosPayType);
            }
        }
        return newList;
    }

    /**
     * 获取redis缓存
     * @param type  0-IOS 1-android和微信
     * @param id
     * @return
     */
    @Override
    public IosPayType findPayInfoById(long id , String type) {
        IosPayType payType = null;
        if("0".equals(type)){
            payType = getRedisPayInfo(RedisKey.ll_ios_pay_type, id);
        }else if("1".equals(type)){
            payType = getRedisPayInfo(RedisKey.android_pay_type, id);
        }
        if(payType == null){
            payType = iosPayTypeMapper.findPayTypeInfoById(id);
        }
        return payType;
    }

    /**
     * 获取IOS支付信息根据IOS商品信息ID
     * @param iosCommodityId
     * @return
     */
    @Override
    public IosPayType findIosPayInfoByIosCommodityId(String iosCommodityId) {
        IosPayType payType = null;
        String ios = redisUtil.get(RedisKey.ll_ios_pay_type);
        if(!StringUtils.isEmpty(ios)){
            List<IosPayType> iosPayTypeList = JSONArray.parseArray(ios, IosPayType.class);
            for(IosPayType iosPayType : iosPayTypeList){
                if(iosPayType.getIosCommodityId().equals(iosCommodityId)){
                    payType = iosPayType;
                }
            }
        }
        if(payType == null){
            payType = iosPayTypeMapper.findIosPayInfo(iosCommodityId);
        }
        return payType;
    }

    /**
     * 获取redis缓存
     * @param redisKey
     * @param id
     * @return
     */
    public IosPayType getRedisPayInfo(String redisKey , long id){
        String payInfo = redisUtil.get(redisKey);
        IosPayType iosPayType = null;
        if(!StringUtils.isEmpty(payInfo)){
            List<IosPayType> list = JSONArray.parseArray(payInfo, IosPayType.class);
            if(list.size()>0){
                for(IosPayType payType : list){
                    if(payType.getId() == id){
                        iosPayType = payType;
                    }
                }
            }
        }
        return iosPayType;
    }
}
