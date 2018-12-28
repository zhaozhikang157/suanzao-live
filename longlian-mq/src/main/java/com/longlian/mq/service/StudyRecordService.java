package com.longlian.mq.service;

import com.longlian.model.StudyRecord;
import com.longlian.model.VisitCourseRecord;

/**
 * Created by admin on 2017/2/24.
 */
public interface StudyRecordService {

    void insertRecord(StudyRecord studyRecord);

    boolean isExist(Long userId, Long courseId);


}
