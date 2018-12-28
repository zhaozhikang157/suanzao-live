package com.longlian.live.service.impl;

import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AppMsgService;
import com.longlian.live.third.service.AppMsgRemote;
import com.longlian.model.AppMsg;
import com.longlian.model.Course;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by pangchao on 2017/2/22.
 */
@Service("appMsgService")
public class AppMsgServiceImpl implements AppMsgService {
    private static Logger log = LoggerFactory.getLogger(AppMsgServiceImpl.class);

    @Autowired
    AppMsgRemote appMsgRemote;


    @Override
    public List<Map> getAppMsgList(long id, Integer pageNum, Integer pageSize) {
        ActResultDto resultDto = appMsgRemote.getAppMsgList(id, pageNum, pageSize);
        if("000000".equals(resultDto.getCode())){
            List<Map> list = (List<Map>) resultDto.getData();
            return list;
        }else{
            return new ArrayList<Map>();
        }
    }

    @Override
    public List<Map> getNewAppMsgTypeInfo(long id,String version) {
        ActResultDto resultDto = appMsgRemote.getNewAppMsgTypeInfo(id,version);
        if("000000".equals(resultDto.getCode())){
            List<Map> list = (List<Map>) resultDto.getData();
            return list;
        }else{
            return new ArrayList<Map>();
        }
    }
    @Override
    public List<Map> getNewAppMsgTypeList(String type,long id, Integer pageNum, Integer pageSize) {
        ActResultDto resultDto = appMsgRemote.getNewAppMsgTypeList(type,id, pageNum, pageSize);
        if("000000".equals(resultDto.getCode())){
            List<Map> list = (List<Map>) resultDto.getData();
            return list;
        }else{
            return new ArrayList<Map>();
        }
    }

    @Override
    public void updateStatusByMsgType(String type,long id) {
        appMsgRemote.updateStatusByMsgType(type,id);
    }

    @Override
    public void deleteAppMsgByIds(Long[] ids) {
        appMsgRemote.deleteAppMsgByIds(StringUtils.join(ids,","));
    }

    @Override
    public Map updateAppMsg(long id) throws Exception {
        ActResultDto resultDto = appMsgRemote.updateAppMsg(id);
        if("000000".equals(resultDto.getCode())){
           Map map = (Map) resultDto.getData();
           return map;
        }else{
            return new HashMap();
        }
    }

    @Override
    public void deleteAppMsg(String id) throws Exception {
        appMsgRemote.deleteAppMsg(id);
    }

    @Override
    public void insertV2(int type, long appId, String content, String titleType, long tableId, String url) {
        AppMsg appMsg = new AppMsg();
        appMsg.setType(type);
        appMsg.setStatus(0);
        appMsg.setCreateTime(new Date());
        appMsg.setContent(content);
        appMsg.setAppId(appId);
        appMsg.setTableId(tableId);
        appMsg.setcAct(url);
        appMsgRemote.insertV2(appMsg);
    }

    @Override
    public long getIsAppMsg(long appId) {
        ActResultDto resultDto = appMsgRemote.getIsAppMsg(appId);
        if("000000".equals(resultDto.getCode())){
            return  Long.parseLong(resultDto.getData()+"");
        }else{
            return 0;
        }
    }

    @Override
    public void readAllMessage(long appId) throws Exception {
        appMsgRemote.readAllMessage(appId);
    }

    @Override
    public void deleteAllAppMsg(long appId) throws Exception {
        appMsgRemote.deleteAllAppMsg(appId);
    }

    @Override
    public void updateMsgCourseStatus(long courseId, String status, long roomId) {
        appMsgRemote.updateMsgCourseStatus(courseId,status,roomId);
    }

    @Override
    public void delAppMsgBefore(Date time, int i) {
        appMsgRemote.delAppMsgBefore(time,i);
    }

    @Override
    public void insertAppMsg(AppMsg appMsg) {
        appMsgRemote.insertAppMsg(appMsg);
    }

    @Override
    public void updateMsgCourseStatues(List<Course> courseList) {
        appMsgRemote.updateMsgCourseStatues(courseList);
    }


}