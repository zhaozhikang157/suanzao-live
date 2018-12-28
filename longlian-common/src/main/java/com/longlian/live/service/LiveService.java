package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.model.Course;
import com.longlian.token.AppUserIdentity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 取直播间相关信息
 * Created by liuhan on 2017-07-12.
 */
public interface LiveService {
    /**
     * 添加直播相关人员
     * @param result
     * @param course
     */
    public void setLiveInfo(Map result , Course course);

    /**
     * 取得收入
     * @param courseId
     * @param appId
     * @return
     */
    public BigDecimal getIncome(Long courseId , Long appId);

    /**
     *  取得显示的人员
     * @param courseId
     *  @param isSeriesSigCourse 否是系列课下的单节课
     * @return
     */
    public List<Map>   getShowUsers( Long courseId  , boolean isSeriesSigCourse);

    ActResultDto getUsersByOffset(Long courseId, Integer offset, Integer pageSize);

    ActResultDto getAllUsers(Long courseId, String ids, Integer pageSize ,  AppUserIdentity token);

    public Set<String> getAllUses(Long courseId);
}
