package com.longlian.live.dao;


import com.huaxin.util.DataGridPage;
import com.longlian.dto.LlAccountTrackDto;
import com.longlian.model.LlAccountTrack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/28.
 */
@Mapper
public interface LlAccountTrackMapper {

    void insert(LlAccountTrack accountTrack);
    /**
     * 收益记录
     *
     * @param id
     * @return
     */
    List<Map> getProfit(long id);
    /**
     * 我的收支明细
     *
     * @param id
     * @return
     */
    List<Map> getWalletsPage(@Param("id") long id, @Param("page") DataGridPage dg);

    /**
     * 课程收益 - 学生的信息
     * @param appId
     * @param courseId
     * @param dg
     * @return
     */
    List<Map> findMyCourseProfitPage(@Param("appId")long appId,@Param("courseId")long courseId,@Param("page")DataGridPage dg);

    /**
     * 电子回单 充值
     * @param page
     * @param llAccountTrackDto
     * @return
     */
    List<LlAccountTrackDto> getRechargePage(@Param("page")DataGridPage page,
                                            @Param("llAccountTrackDto")LlAccountTrackDto llAccountTrackDto);

    /**
     * 电子回单 充值 导出
     * @return
     */
    List<Map> getRecharge(@Param("map")Map map);

    /**
     * 电子回单- 充值 - 详情
     * @param id
     * @return
     */
    Map findOrderInfo(long id);

    List<Map> relayCourseIncomePage(@Param("appId") Long appId, @Param("courseId") Long courseId, @Param("page")DataGridPage dg, @Param("today")boolean today);

    List<Map> relayCourseIncomeStat(@Param("courseId") Long courseId, @Param("appId") Long appId, @Param("today")boolean today);


    List<Map> getWalletsNewPage(@Param("id") long id, @Param("returnMoneyLevel") Integer returnMoneyLevel,@Param("page") DataGridPage dg);

}
