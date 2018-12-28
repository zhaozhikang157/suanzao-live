package com.longlian.appmsg.service;

import com.longlian.model.AppMsg;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AppMsgService {

    /**
     * 查询最大ID
     * @return
     */
    long findAppMsgMaxId();

    List<Map> getAppMsgList(long id, Integer pageNum, Integer pageSize);

    List<Map> getNewAppMsgTypeInfo(long id,String version);

    List<Map> getNewAppMsgTypeList(String type,long id, Integer pageNum, Integer pageSize);

    void updateStatusByMsgType(String type,long id);

    void deleteAppMsgByIds(String ids);

    List<Map> getAppMsgListV2(long id, Integer pageNum, Integer pageSize);

    Map updateAppMsg(long id) throws Exception;

    void deleteAppMsg(String id) throws Exception;

    void insertV2(AppMsg appMsg);

    long getIsAppMsg(long appId);

    void readAllMessage(long appId) throws Exception;

    void deleteAllAppMsg(long appId) throws Exception;

    void updateMsgCourseStatus(long courseId, String status , long roomId);

    void updateMsgCourseStatues(List<Course> course);

    List<AppMsg> findTypeMsg(int offset , int pageSize);

    void updateInfo(long id , long courseId , long roomId);

    void delAppMsgBefore(Date time, int i);

    void insertAppMsg(AppMsg appMsg);


}
