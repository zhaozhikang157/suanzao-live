package com.longlian.console.dao;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.UserCount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/3/7.
 */
public interface UserCountMapper {

    /**
     *用户统计
     * @param page
     * @return
     */
    List<Map> getUserCountPage(@Param(value = "page") DataGridPage page,
                               @Param(value = "machineType") String machineType);
    List<Map> getCountListForPage(@Param("page") DatagridRequestModel requestModel, @Param("map") Map map);
    void deleteById(long id);
    List<UserCount> selectByUserCount(UserCount record);
    int insert(UserCount record);

    long getCountUser(@Param("start") String start, @Param("end") String end, @Param("type") long type);
    BigDecimal getUserCountDouble(@Param("start") String start, @Param("end") String end, @Param("type") long type);
    List<UserCount> getCountChannelusing(@Param("start") String start, @Param("end") String end, @Param("type") long type);
    
    List<Map> getCountSite(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("type") String type);
    List<Map> getCountPageUrl(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("objectId") Long objectId, @Param("type") String type);
    List<Map> getButtonClick(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("type") String type, @Param("objectValue") String objectValue);

    List<UserCount> getButtonClickStatMachine(@Param("date") String date, @Param("type") String type, @Param("objectValue") String objectValue);

    List<UserCount> getButtonClickStatChild(@Param("date") String date, @Param("type") String type, @Param("objectValue") String objectValue);
}