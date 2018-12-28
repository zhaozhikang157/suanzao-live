package com.longlian.live.dao;

import com.longlian.model.Gag;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * Created by admin on 2017/8/7.
 */
@Mapper
public interface GagMapper {

    void setGag(Gag gag);

    Set<String> findUserIdByCourseId(Long courseId);

    int findSameUserId(Gag gag);

    void delGag(Gag gag);

}
