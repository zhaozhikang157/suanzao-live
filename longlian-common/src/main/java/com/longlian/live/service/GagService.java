package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.model.Gag;

/**
 * Created by admin on 2017/8/7.
 */
public interface GagService {

    ActResultDto setGag(Long userId , Long courseId , Long optId);

    ActResultDto delGag(Long userId , Long courseId);

    void setGagMq(Gag gag);

    String findUserIdByCourseId(Long courseId);

    int findSameUserId(Gag gag);

    void delGagMq(Gag gag);
}
