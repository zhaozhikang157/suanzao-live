package com.longlian.console.service;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcelWhaUtil;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.CourseDto;
import com.longlian.model.Course;
import com.longlian.model.CourseImg;
import com.longlian.model.CourseWare;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pangchao on 2017/2/15.
 */
public interface CourseService {
    ExportExcelWhaUtil importExcel(HttpServletRequest req, HttpServletResponse response, Map map, Date startDate, Date endDate);
    ExportExcelWhaUtil platformStreamImportExcel(HttpServletRequest req, HttpServletResponse response, Map map, Date startDate, Date endDate);
    Map getTeacherCourseProfit(Map map);
    List<Map> getCoursePlatformStreamList(DatagridRequestModel requestModel, Map map);

    List<CourseDto>  getListPage(DatagridRequestModel requestModel, Map map);
    /**
     * 获取用户所有下单课程订单
     * @param requestModel
     * @param
     * @return
     */
    List<Map> getCourseOrdersList(DataGridPage requestModel, Map map);
    List<CourseDto>  getListByRoomIdPage(DatagridRequestModel requestModel, Map map);

    CourseDto findById(long id);

    List<Map> getCourseCommentList(long id);

    void updateDown(long id, long optId, String optName)  throws Exception;
    void updateUp(long id, long optId, String optName)  throws Exception;
    void recoveryCourse(long id, long optId, String optName)  throws Exception;
    void del(long id, long optId, String optName)  throws Exception;
    void delOSS(Course course, long optId, String optName)  throws Exception;
    void restore(long id, long optId, String optName)  throws Exception;

    List<Course> getNotEndListBy24Hour();

    List<CourseDto> getCourseMessageTask(DataGridPage page);

    int  updateEndTime(long courseId);

    public List<CourseWare> getCourseWare(Long courseId);

    public List<CourseImg> getCoursImg(Long courseId);

    public Course getCourse(Long courseId);

    Map getCourseDetails(long id);
    public  List<Map> getCourseMessageListPage(DataGridPage page, Long courseId, String liveTopic, String attach);


    List<Course> getPreRemindByHourList();


    List<Course> findDuringThisPeriodCourse(String startTime, String endTime, List<Long> list);

    long addVisitCount(long id, long addCount, long optId, String optName);

    long addJoinCount(long id, long addCount, long optId, String optName);

    long addRecoSort(long id, long addCount, long optId, String optName);
    BigDecimal addTypeSort(long id, BigDecimal addCount, long optId, String optName);


    long setAutoCloseTime(long id, long autoCloseTime, long optId, String optName);
    /**
     * 修改
     *
     * @param Course
     * @return
     */

    void doUpdate(CourseDto Course) throws Exception;

    List<Course> getNoChatRoomId();

    List<CourseDto> getNotEndRecordCourseList();
    List<Map>  getAuditListPage(DatagridRequestModel requestModel, Map map);

    List<Course> getSeriesCourseNotEnd();

    List<Course> getCourseBySeries(long id);

    List<Long> findStatus();

    List<Course> findAll();

    List<Course> findAllCourse(long pageSize, long offset);

    void updateVideoAddress(long id, String videoAddress);

    List<Map> getNeedDealCourse();

    List<Course> findVideoAddress(int pageSize);

    public void delOSSAll(List<Course> courses, long optId, String optName) throws Exception;

    public Course getCourseFromRedis(Long courseId);

    Set<Course> getAllStartCourse();

    Course getCourseByVideoAddress(String md5);

    void updateCourseSort(long courseId, BigDecimal sort);

    List<Course> findSeriesCourseBySeriesId(long id);

    Boolean isHaveWare(long courseId);

    List<Course> getCourseByToday();
    /** 下线课程操作 */
    int updateNoStreamClassDown(Long courseId);

    /*获取今日的没有结束的语音课*/
    List getCourseByVoiceToday();

    /*修改课程是否隐藏*/
    void updateHidden(long id,String isHide);

    List<Course> getSeriesCourseToday();

    Course getCourseByBefore(long courseId);

    /**
     * 获取当天的全部课程系列单节课数量
     * @return
     */
    Map getCourseCountBySeriesInToday();
    /**
     * 获取当天的全部课程转播系列单节课数量
     * @return
     */
    Map<Long,Map> getCourseRelayCountBySeriesToday(Long[] obs);
}
