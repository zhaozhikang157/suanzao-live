package com.longlian.console.service;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.CoursePrivateCardDto;
import com.longlian.model.course.CoursePrivateCard;

import java.util.List;
import java.util.Map;

public interface CoursePrivateCardService {

    List<Map>  getListByPage(DatagridRequestModel requestModel, Map map);

    Map getCoursePrivateCardById(long id);

    CoursePrivateCardDto getCourseByCourseId(long courseId);

    int addOrUpdate(CoursePrivateCard coursePrivateCard);

    int deleteById(long id);


}
