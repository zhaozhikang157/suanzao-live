package com.longlian.live.service.impl;

import com.longlian.live.dao.StudyRecordDetailMapper;
import com.longlian.live.service.StudyRecordDetailService;
import com.longlian.model.StudyRecordDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liuhan on 2017-03-01.
 */
@Service("studyRecordDetailService")
public class StudyRecordDetailServiceImpl implements StudyRecordDetailService {
    @Autowired
    private StudyRecordDetailMapper studyRecordDetailMapper;
    /**
     * 取最近的detail
     *
     * @param courseId
     * @param appId
     * @return
     */
    @Override
    public StudyRecordDetail getStudyRecordDetailRec(Long courseId, Long appId) {
        return studyRecordDetailMapper.selectStudyRecordDetailRec(  courseId,   appId);
    }

    /**
     * 修改最近的detail
     * @return
     */
    @Override
    public void updateStudyRecordDetailRec(StudyRecordDetail studyRecordDetail) {
        studyRecordDetailMapper.updateStudyRecordDetailRec(  studyRecordDetail);
    }

    /**
     * 保存StudyRecordDetail
     *
     * @param studyRecordDetail
     */
    @Override
    public void addStudyRecordDetail(StudyRecordDetail studyRecordDetail) {
        studyRecordDetailMapper.insert(studyRecordDetail);
    }
}
