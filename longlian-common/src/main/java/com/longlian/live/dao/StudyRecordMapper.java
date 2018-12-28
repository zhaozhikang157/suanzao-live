package com.longlian.live.dao;

import com.longlian.model.StudyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface StudyRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StudyRecord record);

    int insertSelective(StudyRecord record);

    StudyRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StudyRecord record);

    int updateByPrimaryKey(StudyRecord record);

    long findByUserIdAndCourseId(@Param("appId") Long userId , @Param("courseId")  Long courseId);
    long findByCourseId(@Param("courseId")  Long courseId);

    List<Map> getCourseStudyUser(Long courseId);
}