package com.longlian.mq.dao;

import com.longlian.model.CourseImg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by pangchao on 2017/3/21.
 */
public interface CourseImgMapper {

    void updateAddressById(@Param("url")String url,@Param("id")long id);

    List<CourseImg> getCourseImgList(long courseId);

}
