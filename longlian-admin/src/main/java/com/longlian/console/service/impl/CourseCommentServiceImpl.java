package com.longlian.console.service.impl;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.console.dao.CourseCommentMapper;
import com.longlian.console.service.CourseCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/25.
 */
@Service("courseCommentService")
public class CourseCommentServiceImpl implements CourseCommentService {
    private static Logger log = LoggerFactory.getLogger(CourseCommentServiceImpl.class);

    @Autowired
    CourseCommentMapper courseCommentMapper;

    @Override
    public List<Map> getListPage(DatagridRequestModel requestModel, Map map) {
        return courseCommentMapper.getListPage(requestModel,map);
    }

    @Override
    public void deleteById(long id) throws Exception {
        courseCommentMapper.deleteById(id);
    }
}