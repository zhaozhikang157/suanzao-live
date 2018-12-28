package com.longlian.console.service;

import com.huaxin.util.page.DatagridRequestModel;

import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/25.
 */
public interface CourseCommentService {

    List<Map> getListPage(DatagridRequestModel requestModel, Map map);
    void deleteById(long id) throws Exception;
}
