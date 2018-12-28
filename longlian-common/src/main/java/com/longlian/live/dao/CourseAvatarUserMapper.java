package com.longlian.live.dao;

import com.longlian.model.CourseAvatarUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface CourseAvatarUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseAvatarUser record);

    int insertSelective(CourseAvatarUser record);

    CourseAvatarUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseAvatarUser record);

    int updateByPrimaryKey(CourseAvatarUser record);

    void importCourseAvatarUser(List<CourseAvatarUser> list);

    void deleteCourseAvatarUser(@Param("userIds")  String ids,@Param("courseId")   Long courseId);

    List<CourseAvatarUser> getUsersByCount(@Param("courseId") Long courseId, @Param("count")  Long count);

    void deleteByCourse(@Param("courseId") Long courseId);

    CourseAvatarUser find(@Param("courseId")  Long courseId,@Param("userId") Long userId);
}