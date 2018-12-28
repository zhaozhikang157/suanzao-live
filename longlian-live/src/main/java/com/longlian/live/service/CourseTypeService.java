package com.longlian.live.service;

import com.longlian.dto.ActResultDto;

import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/14.
 */
public interface CourseTypeService {

    List getCourseType4Redis();

    List<Map<String,String>> getCourseTypes();
}
