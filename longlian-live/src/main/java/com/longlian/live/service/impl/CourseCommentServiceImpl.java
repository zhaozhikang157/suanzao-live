package com.longlian.live.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.constant.MsgConst;
import com.longlian.live.dao.CourseCommentMapper;
import com.longlian.live.dao.CourseMapper;
import com.longlian.live.service.CourseCommentService;
import com.longlian.live.service.SendMsgService;
import com.longlian.model.Course;
import com.longlian.model.CourseComment;
import com.longlian.type.MsgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/17.
 */
@Service("courseCommentService")
public class CourseCommentServiceImpl implements CourseCommentService {

    @Autowired
    CourseCommentMapper courseCommentMapper;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    SendMsgService sendMsgService;

    @Override
    public List<Map> getLastListByCourseId(long courseId) {
        return courseCommentMapper.getLastListByCourseId(courseId);
    }

    @Override
    public void insert(CourseComment courseComment) throws Exception {
        courseCommentMapper.insert(courseComment);
        long courseId = courseComment.getCourseId();
        Course course = courseMapper.getCourse(courseId);
        sendMsgService.sendMsg(course.getAppId(), MsgType.COMMENT_REMIND.getType(), courseId,
                MsgConst.replace(MsgConst.COURSE_COMMENT_REMIND, course.getLiveTopic()), "");

    }

    @Override
    public List<Map> getCommentListByCourseId(Long courseId, Integer offset, Integer pageSize) {
        DataGridPage dg = new DataGridPage();
        if (offset != null) dg.setOffset(offset);
        if (pageSize != null) dg.setPageSize(pageSize);
        List<Map> list = courseCommentMapper.getCommentListByCourseIdPage(courseId, dg);
        if (list.size() > 0) {
            for (Map map : list) {
                map.put("createTime", DateUtil.format((Date)map.get("createTime"),"yyyy-MM-dd HH:mm"));
            }
        }
        return list;
    }
    
    @Override
    public long getCommentSumByCourseId(Long courseId) {
        return courseCommentMapper.getCourseCommentSum(courseId);
    }

    @Override
    public  long getSeriesCourseCommentSum( Long seriesId){
        return courseCommentMapper.getSeriesCourseCommentSum(seriesId);
    }
    
    @Override
    public List<Map> getCommentListByServiesCourseIdPage(Long seriesId, Integer offset, Integer pageSize){

        DataGridPage dg = new DataGridPage();
        if (offset != null) dg.setOffset(offset);
        if (pageSize != null) dg.setPageSize(pageSize);
        List<Map> list = courseCommentMapper.getCommentListByServiesCourseIdPage(seriesId, dg);
        if (list.size() > 0) {
            for (Map map : list) {
                map.put("createTime", DateUtil.format((Date)map.get("createTime"),"yyyy-MM-dd HH:mm"));
            }
        }
        return list;
    }
}
