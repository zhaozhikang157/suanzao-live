package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.model.Course;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.LiveChannel;

import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-02-27.
 */
public interface LiveChannelService {

    public void forbidLiveStream(LiveChannel liveChannel  , Course course) throws Exception;

    public void resumeLiveStream(LiveChannel liveChannel  , Course course) throws Exception;

    public LiveChannel getDefaultLiveChannel();

    /**
     * 取得当前课程用的直播通道
     * @param course
     * @return
     */
    public LiveChannel getCourseLiveAddr( Course course);

    public String getPushAddress(String originUrl, LiveChannel liveChannel , String courseId);

    String getPlayAddress(String roomId, String courseId, LiveChannel mainLiveChannel);

    /**
     * 录制
     * @param course
     * @param domain
     * @param env
     */
    public void saveRecord(Course course , String domain , String env);

    public ActResultDto liveNotify(String courseId , String action  );

    /**
     * 取得课程roomToken
     * @param courseId
     * @param userId
     * @return
     */
    public Map<String , String> getRoomToken(Long courseId  , Long userId) throws Exception;

    /**
     * 删除课程room
     * @param courseId
     * @return
     */
    public void delRoom(Long courseId   ) ;

    public void updateConvert(String courseId);
    public void create(String courseId);
    /**
     * 判断课程下视频流在七牛上是否存在
     * @param courseId
     * @return
     */
    boolean getStreamIsExist(Long courseId);
}
