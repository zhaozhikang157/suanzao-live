package com.longlian.live.service;

import com.longlian.dto.CourseAuditDto;
import com.longlian.model.CourseAudit;

import java.util.List;

/**
 * Created by admin on 2017/9/5.
 */
public interface CourseAuditService {

    void insert(CourseAudit record);
    CourseAudit selectByPrimaryKey(Long id);
    void updateAudit(CourseAudit courseAudit);
    void updateGarbageStatus(Long courseId, String s, String s1);
    public void updateGarbageStatusAndStatus(Long courseId, String remark, String gabageStatus, String status);

    List<CourseAuditDto> getAllNoAudit();

    void updateStatus(Long courseId, String s, String s1);


}
