package com.longlian.live.dao;

import com.longlian.model.StudyRecordDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface StudyRecordDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StudyRecordDetail record);

    int insertSelective(StudyRecordDetail record);

    StudyRecordDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StudyRecordDetail record);

    int updateByPrimaryKey(StudyRecordDetail record);

    /**
     * 查询最新的一条记录
     * @param courseId
     * @param appId
     * @return
     */
    StudyRecordDetail selectStudyRecordDetailRec(@Param("courseId") Long courseId,@Param("appId") Long appId);

    /**
     * 更新最近一条记录
     * @param record
     */
    void updateStudyRecordDetailRec(StudyRecordDetail record);
}