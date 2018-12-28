package com.longlian.live.dao;

import com.longlian.model.VisitCourseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/27.
 */
@Mapper
public interface VisitCourseRecordMapper {

   Map findCourseSource(long courseId);

   void insertRecord(VisitCourseRecord courseRecord);

   long findByUserIdAndCourseId(@Param("userId") Long userId , @Param("courseId")  Long courseId);

   int deleteRecord(@Param("userId") Long userId ,@Param("courseId")  Long courseId);

   List<VisitCourseRecord> findByCourseId(@Param("courseId") long courseId);


}
