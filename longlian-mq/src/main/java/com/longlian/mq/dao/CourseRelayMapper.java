package com.longlian.mq.dao;

import com.longlian.dto.CourseRelayDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018/7/11.
 */
public interface CourseRelayMapper {

    /**
     * 更新转播课访问数量
     * @param appId
     * @return
     */
    void updateRelayVisitCount(@Param("id") long courseId, @Param("visitCount") long count);
}
