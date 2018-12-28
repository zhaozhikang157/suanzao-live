package com.longlian.live.dao;

import com.longlian.model.ShareRecord;
import org.apache.ibatis.annotations.Param;

/**
 * Created by admin on 2017/2/24.
 */
public interface ShareRecordMapper {

    void insertShareRecord(ShareRecord shareRecord);

    int findAppIdAndCourseId(@Param("courseId")long courseId,@Param("appId")long appId);

    int findAppIdAndRoomId(@Param("roomId")long roomId,@Param("appId")long appId);
}
