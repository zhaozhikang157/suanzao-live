package com.longlian.console.service;

import com.huaxin.util.ActResult;
import com.longlian.dto.ActResultDto;

/**
 * Created by admin on 2017/2/24.
 */
public interface VisitCourseRecordService {
    ActResult queryFriendCircleCountByFromType(String beginTime , String endTime);
}
