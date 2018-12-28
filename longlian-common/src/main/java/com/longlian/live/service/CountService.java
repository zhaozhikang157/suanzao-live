package com.longlian.live.service;

import java.math.BigDecimal;

/**
 * 计数接口
 * @author lh
 *
 */
public interface CountService {

    public void newUserCount(Long userId);

    public void newTeacherCount(Long userId);

    public void activeUserCount(Long userId);

    public void payUserCount(Long userId , BigDecimal amt);

    public void newCourseCount(Long courseId);

    public void newPayCourseCount(Long courseId);

    public void newPlatformCourseCount(Long courseId);


}
