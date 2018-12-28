package com.longlian.live.service;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.RecoCourse;
import java.util.List;
import java.util.Map;

public interface RecoCourseServive {

    List<Map> getRecoCourseList(DatagridRequestModel datagridRequestModel, Map map);
    List<Map> getRecoCourses();

    RecoCourse findModelById(Long id);

    void  doSaveAndUpdate(RecoCourse recoCourse) throws Exception;

    void deleteById(Long id);

    public boolean isExistRecoCourse(Long courseId);
}
