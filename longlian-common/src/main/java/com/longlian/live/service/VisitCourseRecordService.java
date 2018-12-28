package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.model.VisitCourseRecord;

/**
 * Created by admin on 2017/2/24.
 */
public interface VisitCourseRecordService {
    /**
     * 普通单节课&系列课
     * @param appId
     * @param courseId
     * @param inviAppId
     * @param type
     * @return
     */
    ActResultDto insertRecord(Long appId, Long courseId, Long inviAppId, String type);

    /**
     * 系列课里的单节课
     * @param appId
     * @param courseId
     * @param inviAppId
     * @param type
     * @param seriesid
     * @param wechatShareType
     * @return
     */
    ActResultDto insertRecord(Long appId, Long courseId, Long inviAppId, String type, Long seriesid, String wechatShareType) ;


    void insertRecord(VisitCourseRecord courseRecord);

    boolean isExist(Long userId , Long courseId);

    int deleteRecord(Long userId , Long courseId);

    void loadVisitRecord2Redis(long courseId);
}
