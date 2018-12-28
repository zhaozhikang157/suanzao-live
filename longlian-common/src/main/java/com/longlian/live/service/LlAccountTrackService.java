package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.LlAccountTrackDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by shuyoulin on 2017/4/28
 */
public interface LlAccountTrackService {
    /**
     * 收益记录
     * @param id
     * @return
     */
    public List<Map> getProfit(long id);

    List<Map> getWalletsPage(long id,Integer returnMoneyLevel, Integer pageSize, Integer offset);

    ActResultDto findMyCourseProfit(Long courseId , Integer pageSize , Integer offset , Long appId);

    List<Map> relayCourseIncomeDetail(Long courseId , Integer pageSize , Integer offset , Long appId);

    List<LlAccountTrackDto> getRechargePage(DataGridPage requestModel, LlAccountTrackDto llAccountTrackDto);

    String exportExcel(Map map, HttpServletRequest request) throws Exception;

    Map findOrderInfo(long id);

    List<Map> relayCourseIncomeStat(Long courseId, Long appId, boolean today);

    List<Map> relayCourseIncomeStat(Long courseId, Long appId);

    List<Map> relayCourseIncomeDetail(Long courseId, Integer pageSize, Integer offset, Long appId, boolean today);
}
