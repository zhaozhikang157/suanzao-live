package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.ActResultDto;
import com.longlian.model.LiveConnect;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LiveConnectService {


    List<Map> getOnlineConnectList(Long courseId,Integer offset, Integer pageSize);

    ActResultDto appLyLiveConnect(Long appId,LiveConnect liveConnect) throws Exception;

    List<Map> getConnectApplyListPage(Long courseId,Integer offset, Integer pageSize);

    List<Map> getLatelyConnectListPage(Long courseId,Integer offset, Integer pageSize);

    void updateStatus(Long connectid, String flag);

    ActResultDto updateStatus(Long connectid, String flag , String erreMsg);

    ActResultDto updateStatusV2(Long courseId , Long applyUser, String flag , String erreMsg);

    LiveConnect getNowConnectByCourseId(Long courseId);

    /**
     * WHA -  The student asked the teacher to connect to the network voice��Request list
     * @param teacher : Teacher ID
     * @param student : Student ID
     * @param courseId :Course ID
     * @param offset :offset
     * @param pageSize :pageSize
     * @return
     */
    ActResultDto getEvenForWheatListPage(Optional<Integer> offset, Optional<Integer> pageSize,  Optional<Long> teacher,  Optional<Long> student,  Optional<Long> courseId);

    /**
     * WHA -  The student asked the teacher to connect to the network voice��Request list
     * @param teacher : Teacher ID
     * @param student : Student ID
     * @param courseId :Course ID
     * @param offset :offset
     * @param pageSize :pageSize
     * @return
     */
    ActResultDto getEvenForWheatEndListPage(Optional<Integer> offset, Optional<Integer> pageSize,  Optional<Long> teacher,  Optional<Long> student,  Optional<Long> courseId);



}
