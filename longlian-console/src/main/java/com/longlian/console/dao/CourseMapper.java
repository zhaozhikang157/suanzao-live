package com.longlian.console.dao;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.CourseDto;
import com.longlian.model.Course;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/15.
 */
public interface CourseMapper {
    Map getTeacherCourseProfit(@Param("map")Map map);
    List<Map> getCoursePlatformStreamListPage(@Param("page") DatagridRequestModel requestModel,@Param("map")Map map);
    Map getCoursePlatformStreamTotalCount(@Param("map")Map map);
    List<CourseDto> getLists(@Param("map")Map map);
    List<Map> getDatasListPage(@Param("page") DatagridRequestModel requestModel,@Param("map")Map map);   //课程详情访问页面 关联数据统计
    List<Map> getDatasList(@Param("page") DatagridRequestModel requestModel,@Param("map")Map map);   //课程详情访问页面 导出 查询所有
    List<Map> getButtonDatasBListPg(@Param("page") DatagridRequestModel requestModel,@Param("map")Map map);  //课程按钮 关联数据统计
    List<Map> getButtonDatasBList(@Param("map")Map map);  //课程按钮 关联数据统计
    List<CourseDto> getListChannelSimplePage(@Param("page") DatagridRequestModel requestModel,@Param("map")Map map);
    Map getListTotalCount(@Param("map")Map map);
    Map findTotalCountByCourseId(@Param("courseId")Long  courseId);
    List<CourseDto> getCourseList(@Param("map")Map map);
    List<Map> getCoursePlatformStreamList(@Param("map")Map map);
    List<CourseDto> getListByRoomIdPage(@Param("page") DatagridRequestModel requestModel,@Param("map")Map map);
    List<Map> getCourseOrdersList(@Param("page") DatagridRequestModel datagridRequestModel,@Param("map") Map map);
    Map getCourseOrdersListTotal(@Param("map") Map map);
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
    Integer getCourseMessageTotal(@Param("courseId")Long courseId,@Param("liveTopic")String liveTopic,@Param("attach")String attach);
    List<Map> getCourseMessagePage(@Param("page")DataGridPage page,@Param("courseId")Long courseId,@Param("liveTopic")String liveTopic,@Param("attach")String attach);
     List<Course> getPreRemindByHourList(@Param("time") String time);


    List<Course> findDuringThisPeriodCourse(@Param("startTime")String startTime,
                                            @Param("endTime")String endTime,
                                            @Param("ids")List<Long> ids);

    void deleteById(long id) throws Exception ;
    void setVisitCount(@Param("id")  long id,@Param("visitCount") Integer visitCount);

    void setJoinCount(@Param("id")  long id,@Param("joinCount") Integer joinCount);

    void setRecoSort(@Param("id")  long id,@Param("recoSort") long recoSort);
    void setTypeSort(@Param("id")  long id,@Param("typeSort") BigDecimal typeSort);
    
    void setAutoCloseTime(@Param("id")  long id,@Param("autoCloseTime") Integer autoCloseTime);
    
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
    List<Map> getAuditListPage(@Param("page") DatagridRequestModel requestModel,@Param("map")Map map);

    List<Course> getSeriesCourseNotEnd();

    List<Course> getCourseBySeries(@Param("seriesId")  long id);

    List<Long> findStatus();

    List<Course> findAll();

    List<Map> getNeedDealCourse();

    List<Course> findAllCourse(@Param("pageSize")long pageSize , @Param("offset")long offset);

    void updateVideoAddress(@Param("id")long id , @Param("videoAddress")String videoAddress);

    List<Course> findVideoAddress(int pageSize);
    //设置单节课管理 首页显示隐藏功能
    int updateShowHide(Course Course);

    Long getOpenCourseCountsByTime(@Param("startTime")Date startTime, @Param("endTime")Date endTime);
    /**
     * 设置单节课转播市场显示隐藏
     * @param course
     * @return
     */
	int updateShowRelayHide(Course course);
	/**
	 * 查询课程列表的总数
	 * @param map
	 * @return
	 */
	Map getListTotal(@Param("map")Map map);
}
