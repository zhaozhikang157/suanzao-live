package com.longlian.console.service;
import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcelWhaUtil;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.PageUrl;
import com.longlian.model.UserCount;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/3/7.
 */
public interface UserCountService {
    public List<Map> getUserCountPage(DataGridPage page, String machineType);
    void deleteById(long id) throws Exception ;
    Map getDateRangeMap(Map requestMap);
    Map getClickDateRangeMap(Map requestMap);
    long getCountUser(Date start, Date end, int type);
    List<Map> getCountListForPage(DatagridRequestModel requestModel,Map map);
    BigDecimal getUserCountDouble(Date start, Date end, int type);
    ActResult getLiveChannelUsingCount(String dayTime,String beginTime,String endTime) throws Exception;


    ActResult getLengthOfStay(Long objectId,String beginTime,String endTime) throws Exception;
    ActResult getExitRate(Long objectId,String beginTime,String endTime) throws Exception;

    ActResult getVisit(Long objectId,String beginTime,String endTime) throws Exception;

    ActResult getPageHandleTime(Long objectId,String beginTime,String endTime) throws Exception;

    ActResult getButtonClick(String beginTime,String endTime,String objectValue) throws Exception;


    List<PageUrl> getUrls();

    ActResult getButtonClickDetail(String objectValue , String date);

    List<Map> getCourseDetailDataStatistics(DatagridRequestModel requestModel,Map map);
    List<Map> getCourseDetailClickDataStatistics(DatagridRequestModel requestModel,Map map);

    ExportExcelWhaUtil importButtonCountExcel(HttpServletRequest req, HttpServletResponse response,Map map);
    ExportExcelWhaUtil importCoursePageCountExcel(HttpServletRequest req, HttpServletResponse response,Map map);
}
