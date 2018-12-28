package com.longlian.live.service;

import com.longlian.dto.ActResultDto;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/10.
 */
public interface CourseManagerService {

    ActResultDto findAllManagersPage(Long teacherId,Integer pageSize , Integer offset);

    ActResultDto findAppUserById(Long userId,Integer pageSize , Integer offset ,
                                 Long appId);

    ActResultDto createCourseManager(Long teacherId , Long userId);

    ActResultDto delCourseManagerById(Long id,Long teacherId);

    ActResultDto delManagerRealById(Long courseId , Long userId , Long appId);

    ActResultDto createManagerReal(Long courseId , Long userId,Long appId);

    String findAllManagerRealByCourseId(Long courseId);

    Boolean isCourseManager(Long courseId , Long userId , String type);



}
