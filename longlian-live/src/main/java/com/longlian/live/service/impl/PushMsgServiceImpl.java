package com.longlian.live.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.AppUserMapper;
import com.longlian.live.dao.PushMsgMapper;
import com.longlian.live.service.PushMsgService;
import com.longlian.live.util.jpush.JPushLonglian;
import com.longlian.model.AppUser;
import com.longlian.model.PushMsg;
import com.longlian.type.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/17.
 */
@Service("pushMsgService")
public class PushMsgServiceImpl implements PushMsgService {
    private static Logger log = LoggerFactory.getLogger(PushMsgServiceImpl.class);

    @Autowired
    PushMsgMapper pushMsgMapper;
    @Autowired
    AppUserMapper appUserMapper;
    @Autowired
    RedisUtil redisUtil;
    @Override
    public List<Map> getPushMsgList(Integer pageNum,Integer pageSize) {
        DataGridPage dg = new DataGridPage();
        if (pageNum!=null) dg.setCurrentPage(pageNum);
        if (pageSize!=null)dg.setPageSize(pageSize);
        List<Map> list = pushMsgMapper.getPushMsgListPage(dg);
        if (list.size()>0){
            for (Map map : list){
                map.put("sendTime", DateUtil.format((Date) map.get("sendTime"), "yyyy-MM-dd HH:mm:ss"));

            }
        }
        return list;
    }

    @Override
    public Map getPushMsgById(Long id) {
        Map map = pushMsgMapper.getPushMsgById(id);
        if (map!=null){
            map.put("sendTime", DateUtil.format((Date) map.get("sendTime"), "yyyy-MM-dd HH:mm:ss"));
        }
        return map;
    }

}