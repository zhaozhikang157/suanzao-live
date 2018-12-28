package com.longlian.appmsg.service.impl;

import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.StringUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.db.DataSource;
import com.huaxin.util.db.DynamicDataSourceKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.appmsg.dao.AppMsgMapper;
import com.longlian.appmsg.service.AppMsgService;
import com.longlian.appmsg.util.JPushLonglian;
import com.longlian.model.AppMsg;
import com.longlian.model.Course;
import com.longlian.type.MsgType;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("appMsgServicce")
public class AppMsgServiceImpl implements AppMsgService {
    private static Logger log = LoggerFactory.getLogger(AppMsgServiceImpl.class);

    @Autowired
    AppMsgMapper appMsgMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public long findAppMsgMaxId() {
        return appMsgMapper.findAppMsgMaxId();
    }

    @Override
    public List<Map> getAppMsgList(long id, Integer pageNum, Integer pageSize) {
        DataGridPage dg = new DataGridPage();
        if (pageNum != null) dg.setOffset(pageNum);
        if (pageSize != null) dg.setPageSize(pageSize);
        List<Map> list = appMsgMapper.getAppMsgListPage(id, dg);
        Date now = new Date();
        SimpleDateFormat sss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowStr = sss.format(now);
        if (list.size() > 0) {
            for (Map map : list) {
                if (map.get("createTime") != null) {
                    map.put("createTime", DateUtil.transFormationStringDate(map.get("createTime").toString(), now, nowStr));
                }
                if (map.get("readTime") != null) {
                    map.put("readTime", DateUtil.format((Date) map.get("readTime"), "yyyy-MM-dd HH:mm:ss"));
                }
                if (map.get("type") != null)
                    map.put("msgType", MsgType.getNameByValue(Integer.valueOf(map.get("type").toString())));
            }
        }
        return list;
    }

    @Override
    public List<Map> getNewAppMsgTypeInfo(long id,String version) {
/*        DataGridPage dg = new DataGridPage();
        if (pageNum != null) dg.setOffset(pageNum);
        if (pageSize != null) dg.setPageSize(pageSize);*/
        List<Map> list=new ArrayList<Map>();
        String []title={"开课提醒","创课提醒","收益提醒","交易通知","活动通知","其它消息"};
        for(int i=0;i<title.length;i++){
            Map map = new HashMap();
            if(i == 4){
                if("2.0.0".equals(version)){
                    continue;
                }
                map = null;
            }else {
                if(i == 5){
                    map = appMsgMapper.getNewAppMsgTypeInfo(id,String.valueOf(i-1),"0");
                }else {
                    map = appMsgMapper.getNewAppMsgTypeInfo(id,String.valueOf(i),"0");
                }
            }
            if(map !=null){
                //总数
                Long count = 0L;
                if(i == 5){
                    count=appMsgMapper.getMsgTypeInfoCount(id,String.valueOf(i-1),"0");
                }else {
                    count=appMsgMapper.getMsgTypeInfoCount(id,String.valueOf(i),"0");
                }
                map.put("acount",count);
            }else {
                map=new HashMap<>();
                map.put("acount",0);
                if(i==5){
                    map.put("tip",i-1);
                }else {
                    map.put("tip",i);
                }
                map.put("createTime","");
                if(i == 4){
                    DataGridPage dg = new DataGridPage();
                    dg.setOffset(0);
                    dg.setPageSize(1);
                    List<Map> list2 = new ArrayList<Map>();
                    list2 = appMsgMapper.getPushAppMsgTypeListPage("103", dg);
                    map.put("title","活动通知");
                    if(null != list2  && list2.size()>0){
                        map.put("content",list2.get(0).get("content"));
                        map.put("createTime",list2.get(0).get("createTime"));
                    }
                    map.put("tip",103);
                }
            }

            //如果没有未读消息内容，查询已读消息内容
            if((map.get("content")==null || "".equals(map.get("content"))) && (Integer)map.get("acount")==0){
                Map mapC = new HashMap();
                if(i == 5){
                    mapC = appMsgMapper.getNewAppMsgTypeInfo(id,String.valueOf(i-1),"1");
                }else {
                    mapC = appMsgMapper.getNewAppMsgTypeInfo(id,String.valueOf(i),"1");
                }
                //如果没有已读消息内容，显示暂无新消息
                if(mapC!=null && mapC.get("content")!=null && !"".equals(mapC.get("content"))){
                    map.put("content",mapC.get("content"));
                    map.put("createTime",mapC.get("createTime"));
                }
            }

            if((map.get("content")==null || "".equals(map.get("content")))){
                map.put("content","暂无新消息");
            }
            map.put("title",title[i]);
            list.add(map);
        }

        Date now = new Date();
        SimpleDateFormat sss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowStr = sss.format(now);
        if (list.size() > 0) {
            for (Map map : list) {
                if (map.get("createTime") != null && !"".equals(map.get("createTime"))) {
                    map.put("createTime", DateUtil.transFormationStringDate(map.get("createTime").toString(), now, nowStr));
                }
                if (map.get("readTime") != null && !"".equals(map.get("readTime"))) {
                    map.put("readTime", DateUtil.format((Date) map.get("readTime"), "yyyy-MM-dd HH:mm:ss"));
                }
                if (map.get("type") != null && !"".equals(map.get("type")))
                    map.put("msgType", MsgType.getNameByValue(Integer.valueOf(map.get("type").toString())));
            }
        }

        return list;
    }

    @Override
    public List<Map> getNewAppMsgTypeList(String type,long id, Integer pageNum, Integer pageSize) {
        DataGridPage dg = new DataGridPage();
        if (pageNum != null) dg.setOffset(pageNum);
        if (pageSize != null) dg.setPageSize(pageSize);
        if(type!=null && "".equals(type)){
            type=null;
        }
        List<Map> list = new ArrayList<>();
        if("103".equals(type)){
            list = appMsgMapper.getPushAppMsgTypeListPage(type, dg);
        }else {
            list = appMsgMapper.getNewAppMsgTypeListPage(type, id, dg);
        }
        Date now = new Date();
        SimpleDateFormat sss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowStr = sss.format(now);
        if (list.size() > 0) {
            for (Map map : list) {
                if (map.get("createTime") != null) {
                    map.put("createTime", DateUtil.transFormationStringDate(map.get("createTime").toString(), now, nowStr));
                }
                if (map.get("readTime") != null) {
                    map.put("readTime", DateUtil.format((Date) map.get("readTime"), "yyyy-MM-dd HH:mm:ss"));
                }
                if (map.get("type") != null)
                    map.put("msgType", MsgType.getNameByValue(Integer.valueOf(map.get("type").toString())));
            }
        }
        return list;
    }

    @Override
    public void updateStatusByMsgType(String type,long id) {
        appMsgMapper.updateStatusByMsgType(type, id);
    }

    @Override
    public void deleteAppMsgByIds(String ids) {
        if(ids!=null && !"".equals(ids)){
            String []idsArr=ids.split(",");
//            long[] strArrNum = (long[]) ConvertUtils.convert(idsArr, long.class);
            Long []strArrNum=new Long[idsArr.length];
            for(int i=0;i<idsArr.length;i++){
                strArrNum[i] = Long.valueOf(idsArr[i]);
            }
            appMsgMapper.deleteAppMsgByIds(strArrNum);
        }
    }

    @Override
    public List<Map> getAppMsgListV2(long id, Integer pageNum, Integer pageSize) {
        DataGridPage dg = new DataGridPage();
        if (pageNum != null) dg.setOffset(pageNum);
        if (pageSize != null) dg.setPageSize(pageSize);
        List<Map> list = appMsgMapper.getAppMsgListPage(id, dg);
        if (list.size() > 0) {
            for (Map map : list) {
                if (map.get("createTime") != null)
                    map.put("createTime", DateUtil.format((Date) map.get("createTime"), "yyyy-MM-dd HH:mm:ss"));
                if (map.get("readTime") != null)
                    map.put("readTime", DateUtil.format((Date) map.get("readTime"), "yyyy-MM-dd HH:mm:ss"));
                if (map.get("type") != null)
                    map.put("msgType", MsgType.getNameByValue(Integer.valueOf(map.get("type").toString())));
            }
        }
        return list;
    }

    @Override
    public Map updateAppMsg(long id) throws Exception {
        Map map = appMsgMapper.getAppMsg(id);
        if (map != null) {
            if (Long.parseLong(map.get("status").toString()) == 0) {//0：未读 1：已读
                appMsgMapper.updateReadTime(id);
            }
            if (map.get("createTime") != null)
                map.put("createTime", DateUtil.format((Date) map.get("createTime"), "yyyy-MM-dd HH:mm:ss"));
            if (map.get("readTime") != null)
                map.put("readTime", DateUtil.format((Date) map.get("readTime"), "yyyy-MM-dd HH:mm:ss"));
        }
        return map;
    }

    @Override
    public void deleteAppMsg(String id) throws Exception {
        if(StringUtils.isNotEmpty(id)){
            if(id.contains(",")){
                String s[] = id.split(",");
                for(String ids : s){
                    appMsgMapper.deleteAppMsg(Long.valueOf(ids));
                }
            }else{
                appMsgMapper.deleteAppMsg(Long.valueOf(id));
            }
        }
    }

    @Override
    public void insertV2(AppMsg appMsg) {
        redisUtil.lpush(RedisKey.ll_app_msg_wait2db , JsonUtil.toJsonString(appMsg));
    }


    @Override
    public long getIsAppMsg(long appId) {
        return appMsgMapper.getIsAppMsg(appId);
    }

    @Override
    public void readAllMessage(long appId) throws Exception {
        appMsgMapper.readAllMessage(appId);
    }

    @Override
    public void deleteAllAppMsg(long appId) throws Exception {
        appMsgMapper.deleteAllAppMsg(appId);
    }

    @Override
    public void updateMsgCourseStatus(long courseId, String status, long roomId) {
        appMsgMapper.updateMsgCourseStatus(courseId, status, roomId);
    }

    @Override
    public void updateMsgCourseStatues(List<Course> course) {
        try {
            appMsgMapper.updateMsgCourseStatues(course);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<AppMsg> findTypeMsg(int offset, int pageSize) {
        return appMsgMapper.findTypeMsg(offset, pageSize);
    }

    @Override
    public void updateInfo(long id, long courseId, long roomId) {
        appMsgMapper.updateInfo(id, courseId, roomId);
    }

    @Override
    public void delAppMsgBefore(Date time, int i) {
        appMsgMapper.delAppMsgBefore(time, i);
    }

    @Override
    public void insertAppMsg(AppMsg appMsg) {
        if(appMsg.getAppId() % 2 == 0){
            appMsg.setId(redisUtil.incrBy(RedisKey.app_msg_max_id , 1));
        }else{
            appMsg.setId(redisUtil.incrBy(RedisKey.app_msg_max_id , 2));
        }
        appMsgMapper.insert(appMsg);
        MsgType type = MsgType.getMsgTypeByValue(appMsg.getType());
        //学生的没有app不用推送,老师要推送
        if (type.isPush()) {
            Map map = new HashMap();
            map.put("NotificationType",String.valueOf(appMsg.getType().toString()));
            try {
                String codes = JPushLonglian.getCodeByUserId(String.valueOf(appMsg.getAppId()) , redisUtil);
                JPushLonglian.sendPushNotificationByCode(codes,appMsg.getContent(),map);
            } catch (APIConnectionException e) {
                e.printStackTrace();
                log.error("发送推送失败",e);
            } catch (APIRequestException e) {
                e.printStackTrace();
                log.error("发送推送失败",e);
            }
        }
    }
}
