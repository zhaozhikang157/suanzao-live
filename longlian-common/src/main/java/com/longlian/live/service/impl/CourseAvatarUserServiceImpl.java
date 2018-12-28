package com.longlian.live.service.impl;

import com.longlian.live.dao.CourseAvatarUserMapper;
import com.longlian.live.service.CourseAvatarUserService;
import com.longlian.model.CourseAvatarUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuhan on 2017-06-28.
 */
@Service("courseAvatarUserService")
public class CourseAvatarUserServiceImpl implements CourseAvatarUserService {
    @Autowired
    CourseAvatarUserMapper courseAvatarUserMapper;
    @Override
    public void batchInsertUsers(Long courseId, String[] userIds) {
       // List<CourseAvatarUser> list = new ArrayList();

        for (String userId :userIds) {
            CourseAvatarUser user = new CourseAvatarUser();
            user.setCreateTime(new Date());
            user.setCourseId(courseId);
            user.setUserId(Long.parseLong(userId));
            insertUser(courseId, Long.parseLong(userId));
        }
//        if (list.size() > 0 ) {
//            courseAvatarUserMapper.importCourseAvatarUser(list);
//        }
    }

    @Override
    public void insertUser(Long courseId, Long userId) {
        CourseAvatarUser user = courseAvatarUserMapper.find(courseId,  userId);
        //没有才插入
        if (user == null) {
            user = new CourseAvatarUser();
            user.setCreateTime(new Date());
            user.setCourseId(courseId);
            user.setUserId(userId);
            courseAvatarUserMapper.insert(user);
        }
    }

    @Override
    public void batchRemoveUsers(long courseId, String[] userIds) {
        List<CourseAvatarUser> list = new ArrayList();

        StringBuffer sb = new StringBuffer();
        for (String userId :userIds) {
             sb.append(userId).append(",");
        }
        if (userIds.length > 0 ) {
            sb.deleteCharAt(sb.length() - 1);
            courseAvatarUserMapper.deleteCourseAvatarUser(sb.toString() , courseId);
        }
    }

    @Override
    public void deleteAll(long id) {
        courseAvatarUserMapper.deleteByCourse(id);
    }
}
