package com.longlian.live.service;

import com.longlian.model.AppMsg;
import com.longlian.model.Course;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/22.
 */
public interface AppMsgService {

    List<Map> getAppMsgList(long id, Integer pageNum, Integer pageSize);

    List<Map> getNewAppMsgTypeInfo(long id,String version);

    List<Map> getNewAppMsgTypeList(String type,long id, Integer pageNum, Integer pageSize);

    void updateStatusByMsgType(String type,long id);

    void deleteAppMsgByIds(Long[] ids);

    Map updateAppMsg(long id) throws Exception;

    void deleteAppMsg(String id) throws Exception;

    void insertV2(int type, long appId, String content, String titleType, long tableId, String url);

    long getIsAppMsg(long appId);

    void readAllMessage(long appId) throws Exception;

    void deleteAllAppMsg(long appId) throws Exception;

    void updateMsgCourseStatus(long courseId, String status , long roomId);

    void delAppMsgBefore(Date time, int i);

    void insertAppMsg(AppMsg appMsg);

    void updateMsgCourseStatues(List<Course> courses);

}
