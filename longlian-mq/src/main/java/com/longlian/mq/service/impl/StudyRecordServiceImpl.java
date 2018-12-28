package com.longlian.mq.service.impl;

import com.longlian.live.dao.StudyRecordMapper;
import com.longlian.model.StudyRecord;
import com.longlian.mq.service.StudyRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liuhan on 2017-03-01.
 * 学习记录处理实现类
 */
@Service("studyRecordServiceMq")
public class StudyRecordServiceImpl implements StudyRecordService{
    private static Logger log = LoggerFactory.getLogger(StudyRecordServiceImpl.class);

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    @Override
    public void insertRecord(StudyRecord studyRecord) {
        studyRecordMapper.insert(studyRecord);
    }

    @Override
    public boolean isExist(Long userId, Long courseId) {
        long i =  studyRecordMapper.findByUserIdAndCourseId(userId,courseId);
        if (i > 0 ){
            return true;
        } else {
            return false;
        }
    }
}
