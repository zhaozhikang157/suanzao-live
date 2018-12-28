package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.LiveRoomDto;
import com.longlian.model.LiveRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Mapper
public interface LiveRoomMapper {
    LiveRoom findByAppId(long appId);

    int insert(LiveRoom liveRoom);
    /**
     * 更新直播间邀请卡模板编号
     * @param appId
     * @param tempCode
     */
    void updateLiveRoom(@Param("appId") long appId, @Param("tempCode") String tempCode);


    int update(LiveRoom liveRoom);
    void updateLiveRoomRemark(@Param("appId") long appId, @Param("remark") String remark);

    List<Map> getFollowRoom(long appId);

    int setLiveRoom(LiveRoom liveRoom);

    /**
     * 修改直播间模板地址
     * @param appId
     * @param address
     */
    void updateinviCardAddress(@Param("appId") long appId, @Param("address") String address);

    LiveRoom findById(@Param("id") long id);

    int updateByLive(LiveRoom liveRoom);

    /**
     * 直播间收益
     * @param page
     * @param appId
     * @return
     */
    List<Map> getCourseIncomeDetailsPage(@Param("page") DataGridPage page, @Param("appId") long appId);


    /**
     * 转播收益
     * @param page
     * @param appId
     * @return
     */
    List<Map> getRelayCourseIncomeDetailsPage(@Param("page") DataGridPage page, @Param("appId") long appId);

    /**
     * 直播间收益today
     * @param page
     * @param appId
     * @return
     */
    List<Map> getCourseIncomeTodayDetailsPage(@Param("page") DataGridPage page, @Param("appId") long appId);

    /**
     * 直播间总收益 today
     * @param appId
     * @return
     */
    BigDecimal getTodayCourseIncome(@Param("appId") long appId);

    /**
     * 分销收益
     * @param page
     * @param appId
     * @return
     */
    List<Map> getdisIncomeDetailsPage(@Param("page") DataGridPage page, @Param("appId") long appId);

    /**
     * 分销收益(Today)
     * @param page
     * @param appId
     * @return
     */
    List<Map> getdisIncomeTodayDetailsPage(@Param("page") DataGridPage page, @Param("appId") long appId);

    /**
     * 今日分销总收益
     * @param appId
     * @return
     */
    BigDecimal getTodayDisIncome(@Param("appId") long appId);

    BigDecimal getTodayRelayIncome(@Param("appId") long appId);

    Long getLiveRoomId(String liveRoomNo);

    List<LiveRoom> selectAll();

    void updateLiveRoomNo(@Param("id") long id, @Param("uuid") String uuid);

    void updateTemp(@Param("code") String code, @Param("id") long roomId);

    void updateMessageFlag(@Param("messageFlag") String messageFlag, @Param("roomId") long roomId);

    void updateReduceDataCountById(@Param("roomId") long roomId, @Param("balance") long balance);


    void updateSize(@Param("id")Long roomId,@Param("size")Long size);

    void updateDaySize(@Param("id")Long roomId,@Param("size")Long size);

    void updateReviewCount(@Param("id")Long roomId,@Param("size")Long size);

    //后台
    List<Map> getCourseCount();
    List<LiveRoomDto> getPendingListPage(@Param("page")DatagridRequestModel datagridRequestModel, @Param("liveRoom")LiveRoomDto liveRoom);
    List<LiveRoomDto> getAuditedListPage(@Param("page")DatagridRequestModel datagridRequestModel, @Param("liveRoom")LiveRoomDto liveRoom);
    int updateHand(LiveRoomDto liveRoom);
    LiveRoomDto findDtoById(long id);

    List<Map> getAllUseRoom();
    List<LiveRoomDto> getLiveRoomListPage(@Param("page")DatagridRequestModel datagridRequestModel, @Param("liveRoom")LiveRoomDto liveRoom);
    void disableRoom(@Param("roomId")Long roomId,@Param("roomStatus")String roomStatus,@Param("disableRemark") String disableRemark);
    List<String> findAllRoomId();
    void setAutoCloseTime(@Param("id")  long id,@Param("autoCloseTime") Integer autoCloseTime);
    void updateIsShow(@Param("id")  long id,@Param("isShow") Integer isShow);
    int updateRoomByAppId(@Param("appId")  long appId,@Param("name") String name);

    BigDecimal getRelayCourseIncomeTotal(@Param("appId") long id, @Param("today") boolean today);
}