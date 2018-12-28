package com.longlian.console.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.AppUserMapper;
import com.longlian.console.dao.MUserMapper;
import com.longlian.console.dao.PushMsgMapper;
import com.longlian.console.service.PushMsgService;
import com.longlian.live.third.service.AppMsgRemote;
import com.longlian.live.util.jpush.JPushLonglian;
import com.longlian.model.AppMsg;
import com.longlian.model.AppUser;
import com.longlian.model.MUser;
import com.longlian.model.PushMsg;
import com.longlian.res.service.ThirdUserService;
import com.longlian.type.MsgType;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    MUserMapper mUserMapper;
    @Autowired
    private ThirdUserService userService;
    @Autowired
    AppMsgRemote appMsgRemote;

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
    //获取H5推送记录
    @Override
    public DataGridPage getH5PushMsgListPage(DataGridPage dataGridPage, Map map) throws Exception{
        map.put("startIndex",dataGridPage.getOffset());map.put("pageSize",dataGridPage.getLimit());
        List<PushMsg> list = pushMsgMapper.getH5PushMsgListPage(dataGridPage,map);
        if(null != list && list.size()>0){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
            for (PushMsg pushMsg: list) {
                pushMsg.setSendTimeFormat(sdf.format(pushMsg.getSendTime()));
            }
        }
        dataGridPage.setTotal(Integer.parseInt(pushMsgMapper.getH5PushMsgCount(map).get("totalCount")+""));
        dataGridPage.setRows(list);
        return dataGridPage;
    }
    //插入H5推送信息
    @Override
    public int insertH5PushMsg(PushMsg pushMsg) throws Exception{
        String name = userService.getUserName(pushMsg.getId());
        if(StringUtils.isNotBlank(name)){
            pushMsg.setPushUser(name);
        }
        pushMsg.setPushType("1");
        pushMsg.setStatus(0);
        pushMsg.setSendTime(new Date());
        pushMsg.setCreateTime(new Date());
        int status = pushMsgMapper.insert(pushMsg);
        String content = pushMsg.getContent();
        AppMsg appMsg = new AppMsg();
        appMsg.setType(MsgType.H5_PUSH_MESSAGE.getType());
        appMsg.setTableId(0);
        appMsg.setContent(content);
        appMsg.setPushUrl(pushMsg.getPushUrl());
        appMsg.setStatus(1);
        appMsg.setCreateTime(new Date());
        appMsgRemote.insertAppMsg(appMsg);
        Map map = new HashMap();
        map.put("NotificationType",  MsgType.H5_PUSH_MESSAGE.getTypeStr());
        map.put("data",pushMsg.getPushUrl());
        map.put("content",pushMsg.getContent());
        JPushLonglian.sendBroadcastToAll(content,map);
        return status;
    }
}