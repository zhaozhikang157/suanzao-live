package com.longlian.live.service;

import com.longlian.model.StudyRecord;
import com.longlian.model.StudyRecordDetail;

/**
 *
 * Created by liuhan on 2017-03-01.
 */
public interface StudyRecordDetailService {
    /**
     * 取最近的detail
     * @param courseId
     * @param appId
     * @return
     */
    public StudyRecordDetail getStudyRecordDetailRec(Long courseId , Long appId);

    /**
     * 修改最近的detail
     * @return
     */
    public void updateStudyRecordDetailRec(StudyRecordDetail studyRecordDetail);

    /**
     * 保存StudyRecordDetail
     * @param studyRecordDetail
     */
    public void addStudyRecordDetail(StudyRecordDetail studyRecordDetail);
}
