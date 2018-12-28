package com.longlian.live.service;

import com.longlian.model.Course;

/**
 * Created by liuhan on 2017-03-06.
 */
public interface EndLiveService {
    /**
     * 结束直播
     * @param courseId
     */
    public void endLive(Long courseId, Long userId) throws  Exception;

    public void endLive(Course course) throws  Exception;

    /**
     * 无直播流的时候结束直播
     * @param courseId
     */
    public void noLiveStreamEndLive(Long courseId)  throws Exception ;
}
