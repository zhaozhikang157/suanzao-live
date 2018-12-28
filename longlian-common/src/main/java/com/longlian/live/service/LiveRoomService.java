package com.longlian.live.service;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.LiveRoomDto;
import com.longlian.live.util.XunChengUtil;
import com.longlian.model.LiveRoom;
import com.longlian.token.AppUserIdentity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/10.
 */
public interface LiveRoomService {
    LiveRoom findByAppId(long appId);

    ActResultDto updateApply(long appId, Map map ) throws Exception;
    ActResultDto createLiveRoom(AppUserIdentity appUserIdentity, Map map) throws Exception;

    ActResultDto getLiveRoomInfo(long appId);


    void updateLiveRoom(long appId, String tempCode);

    void updateLiveRoomRemark(long appId, String remark);

    ActResultDto getUserTempCode(long appId);


    ActResultDto getFollowRoom(long appId);

    void setLiveRoom(LiveRoom liveRoom);

    Map getLiveRoomById(Long appId, Long liveRoomId);

    LiveRoom findById(long id);

    //直播间收益
    List<Map> getCourseIncomeDetailsPage(Integer offset, Integer pageSize, long appId);

    //转播收益
    List<Map> getRelayCourseIncomeDetailsPage(Integer offset, Integer pageSize, long appId);

    //直播间收益 today
    List<Map> getCourseIncomeTodayDetailsPage(Integer offset, Integer pageSize, long appId);

    //直播间总收益 today
    BigDecimal getTodayCourseIncome(long appId);

    //直播间总收益
    BigDecimal getTodayDisIncome(Long appId);

    //分销收益
    List<Map> getdisIncomeDetailsPage(Integer offset, Integer pageSize, long appId);

    //分销收益（Today）
    List<Map> getdisIncomeTodayDetailsPage(Integer offset, Integer pageSize, long appId);

    String getQrAddress(long roomId, long appId);

    Map getLiveRoomInfoByApp(long id);

    Long getLiveRoomId(String liveRoomNo);

    /**
     * 批量处理所有的liveRoomNo
     */
    void updateBatchLiveRoomNo();

    String[] getRoomAddress(String userId, String roomId);

    void updateMessageFlag(String messageFlag, long roomId);

    void updateReduceDataCountById(long roomId, long balance);

    void updateSize(Long id , Long size);

    void updateDaySize(Long id , Long size);

    void updateReviewCount(Long id , Long size);

    //后台服务类
    List<LiveRoomDto>  getPendingListPage(DatagridRequestModel datagridRequestModel, LiveRoomDto liveRoom);

    List<LiveRoomDto>  getAuditedListPage(DatagridRequestModel datagridRequestModel,LiveRoomDto liveRoom);

    void updateHand(LiveRoomDto liveRoom) throws Exception ;

    LiveRoomDto findDtoById(long id);

    List<Map> getAllUseRoom();

    List<Map> getCourseCount();
    List<LiveRoomDto>  getLiveRoomListPage(DatagridRequestModel datagridRequestModel,LiveRoomDto liveRoom);

    void disableRoom(Long roomId,String roomStatus,String disableRemark);

    List<String> findAllRoomId();

    long setAutoCloseTime(long id, int autoCloseTime , long optId , String optName);

    ActResult updateIsShow(long id,Integer isShow);

    BigDecimal getTodayDisIncome(long appId);

    BigDecimal getRelayCourseIncomeTotal(long id);

    BigDecimal getRelayCourseIncomeTotal(long id, boolean today);

    BigDecimal getTodayRelayIncome(long id);
}
