package com.longlian.live.service;

import com.longlian.model.StudyRecordDetail;

import java.util.Date;

/**
 *
 * Created by liuhan on 2017-03-01.
 */
public interface StudyRecordService {
    /**
     * 学习统计
     * @param courseId
     * @return
     */
    public long getStudyRecordCount(Long courseId);


    void addUser2Redis(Long courseId, Long appId, Date date, boolean b);

    public long getCountByCourseId(Long courseId);

    /**
     * 加载对应的加入课程的人员到redis
     * @param courseId
     */
    public void loadCourseUser(Long courseId);
}
