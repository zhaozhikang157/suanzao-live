package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.AdvertisingDisplayDto;
import com.longlian.live.dao.AdvertisingDisplayMapper;
import com.longlian.live.interceptor.UpdateSeriesCourseTime;
import com.longlian.live.service.AdvertisingDisplayService;
import com.longlian.model.AdvertisingDisplay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syl on 2016/8/10.
 */
@Service("advertisingDisplayService")
public class AdvertisingDisplayServiceImpl implements AdvertisingDisplayService {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    AdvertisingDisplayMapper advertisingDisplayMapper;

    /**
     * 查询广告列表
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<AdvertisingDisplayDto> getList(String type) {
//        redisUtil.del(RedisKey.ll_live_app_advertising);
        boolean isExistsKey = false;
        if ("0".equals(type)) {
            //先从redis中取数据
            isExistsKey = redisUtil.exists(RedisKey.ll_live_app_advertising);//先取redis取
        }
        List<AdvertisingDisplayDto> list = null;
        if (isExistsKey) {
            list = getObjList(type);
            return list;
        }
        //没有从数据库中取值，且存入缓存
        List<AdvertisingDisplay> advertisingDisplays = advertisingDisplayMapper.getList(type);
        List<AdvertisingDisplayDto> displayDtoList = new ArrayList<AdvertisingDisplayDto>();
        for(AdvertisingDisplay advertisingDisplay : advertisingDisplays){
            AdvertisingDisplayDto displayDto = new AdvertisingDisplayDto();
            Map map = JsonUtil.getObject(advertisingDisplay.getCourseInfo(), Map.class);
            if (map == null) {
                map = new HashMap<>();
            }
            displayDto.setMapCourseInfo(map);
            displayDto.setCourseInfo(advertisingDisplay.getCourseInfo());
            displayDto.setCreateTime(advertisingDisplay.getCreateTime());
            displayDto.setAdvertType(advertisingDisplay.getAdvertType());
            displayDto.setCourseId(advertisingDisplay.getCourseId());
            displayDto.setId(advertisingDisplay.getId());
            displayDto.setName(advertisingDisplay.getName());
            displayDto.setOpenUrl(advertisingDisplay.getOpenUrl());
            displayDto.setPicAddress(advertisingDisplay.getPicAddress());
            displayDto.setRemarks(advertisingDisplay.getRemarks());
            displayDto.setSortOrder(advertisingDisplay.getSortOrder());
            displayDto.setStatus(advertisingDisplay.getStatus());
            displayDto.setType(advertisingDisplay.getType());
            displayDto.setSystemType(advertisingDisplay.getSystemType());
            displayDto.setIsSeriesCourse(advertisingDisplay.getIsSeriesCourse());
            displayDtoList.add(displayDto);
        }
        resetRedisData(displayDtoList, type);
        return displayDtoList;
    }

    @Override
    @UpdateSeriesCourseTime
    public void test() {

    }

    /**
     * 将从redis取出的list 字符串 转对象
     *
     * @return
     */
    public List<AdvertisingDisplayDto> getObjList(String type) {
        List<AdvertisingDisplayDto> list = new ArrayList<AdvertisingDisplayDto>();
        List<String> arg = new ArrayList<String>();
        if ("0".equals(type)) {
            arg = redisUtil.lrangeall(RedisKey.ll_live_app_advertising);
        }
        if (arg == null) return list;
        for (String temp : arg) {
            if (!Utility.isNullorEmpty(temp)) {
                AdvertisingDisplayDto advertisingDisplay = JsonUtil.getObject(temp, AdvertisingDisplayDto.class);
                list.add(advertisingDisplay);
            }
        }
        return list;
    }

    /**
     * 重新设置redis 数据
     */
    public void resetRedisData(List<AdvertisingDisplayDto> list, String type) {
        if ("0".equals(type)) {
            boolean isExistsKey = redisUtil.exists(RedisKey.ll_live_app_advertising);//先去redis取
            if (isExistsKey) redisUtil.del(RedisKey.ll_live_app_advertising);
            List<String> redisList = new ArrayList<String>();
            for (AdvertisingDisplay resource : list) {
                redisList.add(JsonUtil.toJson(resource));
            }
            redisUtil.rpushlist(RedisKey.ll_live_app_advertising, redisList);
        }
    }
}
