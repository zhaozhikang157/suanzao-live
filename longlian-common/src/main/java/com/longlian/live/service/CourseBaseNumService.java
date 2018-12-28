package com.longlian.live.service;

import com.longlian.model.CourseBaseNum;
import org.apache.ibatis.annotations.Param;

/**
 * Created by liuhan on 2017-07-14.
 */
public interface CourseBaseNumService {
    CourseBaseNum selectByCourse( Long courseId , String type);

    public void updateBaseNum(Long id  , Long roomId, Long addCount);
}
