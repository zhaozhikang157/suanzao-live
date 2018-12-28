package com.longlian.third.service;

import com.huaxin.util.DataGridPage;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.model.Course;
import com.longlian.model.CourseWare;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by syl on 2017/3/24.
 */
public interface CourseService {
    Course getById(long id);
}
