package com.longlian.console.dao;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.CourseDto;
import com.longlian.model.Course;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pangchao on 2017/2/15.
 */
public interface CourseMapper {
    Map getTeacherCourseProfit(@Param("map") Map map);
    List<Map> getCoursePlatformStreamListPage(@Param("page") DatagridRequestModel requestModel, @Param("map") Map map);
    List<CourseDto> getListPage(@Param("page") DatagridRequestModel requestModel, @Param("map") Map map);
    Map findTotalCountByCourseId(@Param("courseId") Long courseId);
    List<CourseDto> getCourseList(@Param("map") Map map);
    List<Map> getCoursePlatformStreamList(@Param("map") Map map);
    List<CourseDto> getListByRoomIdPage(@Param("page") DatagridRequestModel requestModel, @Param("map") Map map);
    List<Map> getCourseOrdersListPage(@Param("page") DatagridRequestModel datagridRequestModel, @Param("map") Map map);
    CourseDto findById(long id);

    void updateDown(long id);
    void updateUp(long id);
    void recoveryCourse(long id);

    List<CourseDto> getCourseMessageTaskPage(@Param("page") DataGridPage page);

    List<Course> getNotEndListBy24Hour();
    List<Course> getNoChatRoomId();

    int updateEndTime(@Param("id") Long courseId);

    Course getCourse(@Param("id") long id);

    Map getDistributionInformation(long courseId);

    List<Map> getCourseMessagePage(@Param("page") DataGridPage page, @Param("courseId") Long courseId, @Param("liveTopic") String liveTopic, @Param("attach") String attach);
     List<Course> getPreRemindByHourList(@Param("time") String time);


    List<Course> findDuringThisPeriodCourse(@Param("startTime") String startTime,
                                            @Param("endTime") String endTime,
                                            @Param("ids") List<Long> ids);

    void setVisitCount(@Param("id") long id, @Param("visitCount") Integer visitCount);

    void setJoinCount(@Param("id") long id, @Param("joinCount") Integer joinCount);

    void setRecoSort(@Param("id") long id, @Param("recoSort") long recoSort);
    void setTypeSort(@Param("id") long id, @Param("typeSort") BigDecimal typeSort);
    
    void setAutoCloseTime(@Param("id") long id, @Param("autoCloseTime") Integer autoCloseTime);
    
    void restore(long id);

    void del(long id);

    void dels(@Param("courses") List<Course> courses);

    /**
     * 修改
     *
     * @param 
     * @return
     */
    int update(Course Course);

    List<CourseDto> getNotEndRecordCourseList();
    List<Map> getAuditListPage(@Param("page") DatagridRequestModel requestModel, @Param("map") Map map);

    List<Course> getSeriesCourseNotEnd();

    List<Course> getCourseBySeries(@Param("seriesId") long id);

    List<Long> findStatus();

    List<Course> findAll();

    List<Map> getNeedDealCourse();

    List<Course> findAllCourse(@Param("pageSize") long pageSize, @Param("offset") long offset);

    void updateVideoAddress(@Param("id") long id, @Param("videoAddress") String videoAddress);

    List<Course> findVideoAddress(int pageSize);

    Set<Course> getAllStartCourse();

    Course getCourseByVideoAddress(@Param("md5") String md5);

    void updateCourseSort(@Param("courseId") long courseId, @Param("sort") BigDecimal sort);

    List<Course> findSeriesCourseBySeriesId(long id);

    List<Course> getCourseByToday();
    /** 下线课程操作 */
    int updateNoStreamClassDown(@Param("courseId") Long courseId);

    /*获取今日的没有结束的语音课*/
    List<Course> getCourseByVoiceToday();

    /*更新课程隐藏*/
    void updateHidden(@Param("id") long id,@Param("isHide") String isHide);

    List<Course> getSeriesCourseToday();

    Course getCourseByBefore(@Param("courseId") long courseId);

    List<Map> getCourseCountBySeriesInToday();

    List<Map> getCourseRelayCountBySeriesToday(@Param("obs") Long[] obs);
}
