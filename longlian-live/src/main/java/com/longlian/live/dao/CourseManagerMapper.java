package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.CourseManager;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/10.
 */
public interface CourseManagerMapper {

    List<Map> findAllManagersPage(@Param("teacherId")Long teacherId , @Param("page")DataGridPage page);

    List<Map> findAppUserByIdPage(@Param("id")Long id,@Param("page")DataGridPage page,
                                  @Param("appId")Long appId);

    int findManagerByTeacherIdAndUserId(@Param("teacherId")Long teacherId , @Param("userId")Long userId);

    void creatrManager(CourseManager courseManager);

    void delCourseManagerById(Long id);

    List<CourseManager> findAllManager(Long teacherId);

    List<Map> findManagereByTeacherId(Long teacherId);

    CourseManager findById(long id);
}
