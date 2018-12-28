package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.CourseDto;
import com.longlian.model.Course;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CourseMapper {

    long insert(Course record);

    int update(Course record);

    /**
     * 老师创建的直播间信息
     * @param appId
     * @param page
     * @return
     */
    List<Map> noLiveInfoPage(@Param("appId") long appId,@Param("page") DataGridPage page);

    /**
     * 老师已经直播过的课件
     * @param appId
     * @param page
     * @return
     */
    List<Map> getAlreadyLivePage(@Param("appId") long appId,@Param("page") DataGridPage page);


    int setLiveRoomInfo(Course course);

    Course getCourse(@Param("id") long id);

    Course getCourseByAppidAndId(@Param("id") long id,@Param("appId") long appId);

    List<Map> getCourseListPage(@Param("page")DataGridPage dg,@Param("liveRoomId")long liveRoomId,@Param("status")String status);

    List<Map> getCourseLIstV164Page(@Param("page")DataGridPage dg,@Param("liveRoomId")long liveRoomId,@Param("status")String status);

    //预告 首页
    List<Map> getPrevueLive4Home(@Param("limit")long limit);
    //预告 首页（分页）V2
    List<Map> getPrevueLive4HomeV2Page(@Param("page")DataGridPage  dg , @Param("isRecorded") String isRecorded);
    //预告 首页（分页）
    List<Map> getPrevueLive4HomePage(@Param("page")DataGridPage  dg);
    //直播中 首页
    List<Map> getLiveing4Home(@Param("limit")long limit);
    //直播中 首页(分页)
    List<Map> getLiveing4HomePage(@Param("page")DataGridPage  dg);
    //直播中 首页(分页)V2
    List<Map> getLiveing4HomeV2Page(@Param("page")DataGridPage  dg, @Param("isRecorded") String isRecorded);
    //推荐 首页(分页)
    List<Map> getCommend4HomePage(@Param("page") DataGridPage page);
    //推荐 首页(分页)V2
    List<Map> getCommend4HomeV2Page(@Param("page") DataGridPage page, @Param("isRecorded") String isRecorded);
    //推荐 首页
    List<Map> getCommend4Home(@Param("limit")long limit);

    List<Course> getAllCourse();

    Map getCourseInfoDetails(long id);

    /**
     * 转博课
     * @param id
     * @return
     */
    Map getRelayCourseInfo(long id);

    List<Map> getLateList(@Param("appId") long  appId,@Param("courseId") long courseId );

    int getCourseCountByAppId(long id);

    void updateEndTime(@Param("id")  Long courseId);

    Long getRoomIdById(long id);

    List<Map> findCourseByNamePage(@Param("name") String name,@Param("page") DataGridPage page);

    List<Map> findCourseByNameV2Page(@Param("name") String name,@Param("page") DataGridPage page ,  @Param("isRecorded") String isRecorded);

    void updateRecoTime(@Param("recoTime")Integer recoTime , @Param("id")Long id);

    int uploadCourse(@Param("course") CourseDto course);

    int uploadSeriesCourse(@Param("course") CourseDto course);

    List<CourseDto> getCourseListByLiveRoom(long id);

    Map getCourseInfoById(long courseId);

    int getCountByRoomId(long roomId);

    List<Map> getMoreLiveCoursePage(@Param("courseTypeId")Long courseTypeId,@Param("page") DataGridPage page);

    List<Map> getMoreLiveCourseNowPage(@Param("courseTypeId")Long courseTypeId,@Param("page") DataGridPage page);

    long getStudyNum(long courseId);
    long getSeriesStudyNum(long courseId);

    Course getCourseByAppId(long appId);

    int setCourseDown(long id);

    int setRelayCourseDown(long id);

    int setRelaySeriesCourseDown(long id);

    List<Map> findDoingAndNotCourse(long appId);

    List<Map> findDoingAndNotCoursePage(@Param("appId")long appId,@Param("page")DataGridPage page);

    List<Map> findAlreadyCoursePage(@Param("appId")long appId,@Param("page")DataGridPage page);

    List<Map> findAlreadyCourse(long appId);

    Map findCourseInfoById(long courseId);

    void updateTemp(@Param("code")String code , @Param("id")long courseId);

    List<Map> findNoLeanCourseByRoomIdPage(@Param("roomId") long roomId,@Param("page") DataGridPage page,  @Param("isRecorded") String isRecorded);

    List<Map> findAlreadyCourseLearnByRoomIdPage(@Param("roomId") long roomId,@Param("page") DataGridPage page,  @Param("isRecorded") String isRecorded);
    int getAlreadyCourseCountByAppId(long id);
    int getNotCourseCountByAppId(long id);
    List<Map> getMyCourseListPage(@Param("seriesCourseId") long seriesCourseId, @Param("page") DataGridPage dg, @Param("isRecorded") String isRecorded);
    List<Map> getMyRelayCourseListPage(@Param("seriesCourseId") long seriesCourseId, @Param("page") DataGridPage dg, @Param("isRecorded") String isRecorded);

    int getSerisCourseCount(long roomId);

    void closeSeries(long courseId);

    int getCoursePlanById(@Param("id")long id , @Param("appid")long appid);
    int getSeriesSingleCourseBySeriesId(@Param("id")long id , @Param("appid")long appid);
    Course  getSeriesById(Long id);
    List<Course> getAllNoEndCourseByTeahcerId(Long teacherId);

    List<Course> findSeriesCourseBySeriesId(long id);

    void clearScreenByChatRoomId(@Param("chatRoomId")long chatRoomId , @Param("cleanScreenTime")long cleanScreenTime);


    Map getLastCourseType(@Param("seriesid") Long seriesid);

    /**
     * 获取所有的单节课
     * @param appId
     * @return
     */
    List<Map> getAllSingleClass(long appId);

    /**
     * 获取所有的系列课
     * @param appId
     * @return
     */
    List<Map> getAllSeriesClass(long appId);
    List<CourseDto> getListPage(@Param("page") DatagridRequestModel requestModel,@Param("map")Map map);

    List<Map> getPcCourseListPage(@Param("page") DataGridPage page,@Param("map")Map map);
    List<Map> getAllPcSeriesClass(long appId);
    void del(long id);
    void updateUp(long id);

    List<Course> getLivingForCourse(long appId);
    List<Course> getshortTimeCourse(long roomId);

    void setShareTime(@Param("mustShareTime")long mustShareTime , @Param("id")long courseId);

    /**
     * WHA
     * @param liveTopic 搜索使用字段 直播主题
     * @param page  分页参数
     * @param isSeriesCourse  是否系列课 1-是系列课0-是单节课
     * @return
     */
    List<Map> findCourseByLiveTopicPage(@Param("liveTopic") String liveTopic,@Param("page") DataGridPage page ,  @Param("isSeriesCourse") String isSeriesCourse,@Param("type") String type);

    /**
     * WHA
     * @param name 搜索使用字段 直播间名称
     * @param page 分页参数
     * @return
     */
    List<Map> findLiveRoomByNamePage(@Param("name") String name,@Param("page") DataGridPage page);
    void updateCanConnect(@Param("courseId")long courseId , @Param("isCan")String isCan);

    /**
     * WHA
     * 单节课OR系列课 每周精选
     * @param id
     * @param
     * @return
     */
    Map findCourseWeeklySelectionById( @Param("id") long id);

    /**
     * WHA
     * 首页加载4条 单节课OR系列课 精选课程
     * @param
     * @return
     */
    List<Map> findCourseAllSelectionPage(@Param("page") DataGridPage page,@Param("map")Map map);
    List<Map> getMyHistoryCourseListPage(@Param("page")DataGridPage  dg, @Param("appId") Long appId, @Param("clientType") String clientType);
    List<Map> getMyBuyFreeCourseListPage(@Param("page")DataGridPage  dg, @Param("appId") Long appId, @Param("clientType") String clientType);
    List<Map> getMyhaveLivedCourseListPage(@Param("page")DataGridPage  dg, @Param("appId") Long appId, @Param("clientType") String clientType);
    List<Map> getMynoLivedCourseList(@Param("appId") Long appId, @Param("clientType") String clientType);
    Map getSeriesCourseInfoBySeriesId(Long couerseId);


    /**
     * 免费专区
     * @param dg
     * @param sort 综合排序--0 人气排序--1 时间排序--2
     * @param sc 时间正序--0 时间倒序--1
     * @return
     */
    List<Map> findFreeAdmissionCoursePage(@Param("page")DataGridPage  dg,@Param("sort") String sort,@Param("sc") String sc);

    /**
     * 每周精选--更多（分页）
     * @param dg
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map> findWeeklySelectionPage(@Param("page")DataGridPage  dg,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("isTime") String isTime);

     /**
     * 正在直播（分页）
     * @param dg
     * @param startTime
     * @return
     */
    List<Map> findBeingroadcastivePage(@Param("page")DataGridPage  dg,@Param("startTime") String startTime);

    /**
     * 未开播（分页）
     * @param dg
     * @param startTime
     * @return
     */
    List<Map> getCommend4HomeV5Page(@Param("page")DataGridPage  dg,@Param("startTime") String startTime);

    /**
     * 排行榜
     * @param dg
     * @param sort 综合排序--0 人气排序--1 时间排序--2 价格 --3
     * @param sc 正序--0 倒序--1
     * @return
     */
    List<Map> findRankingListPage(@Param("page")DataGridPage  dg,@Param("sort") String sort,@Param("sc") String sc);

    Course findSeriesCourseOrderTime(long id);

    List<Course> getCourseBySeriesId(long seriesCourseId);

    Course selectCourseMsgByChatRoomId(long chatRoomid);


    /**
     * 根据课程类型查询课程列表
     * @param dg
     * @return
     */
    List<Map> getCoursesByTypePage(@Param("page")DataGridPage  dg,@Param("courseType") String courseType);

    /**
     * 获取课程状态
     * @param id
     * @return
     */
    Map getCourseStatus(@Param("id") long id);

    /**
     * 获取课程详细信息
     * @param id
     * @return
     */
    Course getCourseDetial(@Param("id") long id);

    Map<String,Object> findBeingroadcastivePageSize(@Param("id") Long id);

    void updateSeriesStartTime(Course course);

    long getCourseCount(@Param("appId")long appid);

    long getSeriesCourseCount(@Param("appId")long appid);

    /**
     * 获取所有可以转播的课程
     * @param sc  排序  正序--0 , 倒序--1
     * @param sort   综合排序  时间--1,价格--2,分成比例--3
     * @param dg
     * @return
     */
    List<Map> queryCanBroadcastCoursePage(@Param("courseName")String courseName,@Param("appId")long appId,@Param("sc")String sc,@Param("sort")String sort, @Param("page")DataGridPage dg);

    /**
     *修改课程转播信息
     * @param course
     */
    void updateCourseRelayInfo(Course course);

    /**
     * 修改课程是否被转播过
     * @param i
     * @param id
     */
    void updateIsOpenById(@Param("i")int i, @Param("id")long id);

    List<Course> getSeriesCourseList(@Param("id")Long id);
}
