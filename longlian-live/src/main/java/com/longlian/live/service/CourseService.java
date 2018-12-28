package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.model.Course;
import com.longlian.model.CourseImg;
import com.longlian.model.CourseWare;
import com.longlian.model.course.CourseCard;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/12.
 */
public interface    CourseService {

    ActResultDto noLiveInfoPage(Integer offset, Integer pageSize ,HttpServletRequest request);


    ActResultDto getAllLiveInfoPage(Integer noLearnOffset, Integer pageSize , long appId , Integer alreadyLearnOffset);

    public Course getCourse(Long courseId);

    public Course getCourseFromRedis(Long courseId);

    List getCourseList(long liveRoomId,Integer pageNum,Integer pageSize , boolean isHaveRecord, String status, String v);

    List<CourseImg> getCourseImgList(long courseId);


    public List<CourseWare> getCourseWare(Long courseId);


    List<Map> getPrevueLive4Home(Integer prevueOffset);


    List<Map> getPrevueLive4HomeV2(Integer prevueOffset , boolean isHaveRecord);


    List<Map> getLiveing4Home(Integer LiveingOffset);

    List<Map> getLiveing4HomeV2(Integer liveingOffset, boolean isHaveRecord);

    List<Map> getCommend4HomePageV2(DataGridPage page, boolean isHaveRecord);

    List<Map> getCommend4HomePage(DataGridPage page);


    public List<Course> getAllCourse();

    public void updateCourseInfo(Course course);

    Map  getCourseDetails(Long courseId);


    ActResultDto getCourseInfo(long id  ,long appid ,boolean isWechatClient,String v,String type);
    ActResultDto getRelayCourseInfo(long id  ,long appid ,boolean isWechatClient,String v);
    long getRoomIdById(long id);

    ActResultDto findCoursebyName(String name ,Integer offset, Integer pageSize);

    ActResultDto findCoursebyNameV2(String name ,Integer offset, Integer pageSize, boolean isHaveRecord);


    ActResultDto getRecTimeByCourseId(Long courseId);

    ActResultDto setRecoTimeById(Long courseId , Integer recoTime);


    boolean getLivePwd(long id,String  livePwd);



    ActResultDto getCourseWareById(Long id);

    List<CourseDto> getCourseListByLiveRoom(long id);

    Map getTeacherSetByCourseId(long courseId,long appId);

    ActResultDto findCourseInfoById(Long courseId,long appId);

    ActResultDto getCoursePushAddr(Long courseId,Long appId , String machineModel);

    ActResultDto getCourseLiveAddr(Long courseId);

    List<Map> getMoreLiveCourse(Long liveStatus,Long courseTypeId,Integer pageNum,Integer pageSize);

    Map getCourseIsFreeSign(Long courseId);

    ActResultDto setCourseDown(long id,long appId) throws Exception;

    ActResultDto getAllCourses(long id);

    ActResultDto getMoreCoursePage(long appId,String type , Integer pageSize , Integer offset);

    Map findCourseInfoById(long courseId);

    ActResultDto findCourseByRoomId(Integer noLearnOffset, Integer pageSize , Long roomId , Integer alreadyLearnOffset , boolean isHaveRecord);

    ActResultDto getSeriesCourseInfo(long seriesid ,long appid ,boolean isWechatClient,String v);

    List<Map> getMyCourseList(long seriesCourseId,long appid,Integer pageNum, Integer pageSize, boolean isHaveRecord , boolean isOldVersion);

    /**
     * 转播课
     * @param seriesCourseId
     * @param appid
     * @param pageNum
     * @param pageSize
     * @param isHaveRecord
     * @param isOldVersion
     * @return
     */
    List<Map> getMyRelayCourseList(long seriesCourseId,long appid,Integer pageNum, Integer pageSize, boolean isHaveRecord , boolean isOldVersion);



    ActResultDto closeSeries(Long courseId,Long appId);


    int getSeriesSingleCourseBySeriesId(long id ,long appid);

    ActResultDto limitedCoursePlanCount(Long seriesid,Long appid) throws Exception;

    Map getShareCourseTitle(long courseId);



    ActResultDto clearScreenByChatRoomId(long courseId , Long userId);

    String getLastCourseType(Long seriesid);

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
    List<Map> getAllseriesClass(long appId);

    List<Map>  getPcCourseListPage(DataGridPage dataGridPage,Map map);

    public List<CourseImg> getCoursImg(Long courseId);
    List<Map> getAllPcSeriesClass(long appId);
    void del(long id, long optId , String optName)  throws Exception;
    void updateUp(long id, long optId , String optName)  throws Exception;
    String getCourseStatusByCourseId(Long courseId);
    ActResultDto  isSameCoureForLiving(Long appId);
    void setCourseDown(Course id);
    void setRelayCourseDown(CourseRelayDto course);
    List<Course>  getLivingForCourse(Long appId);
    List<Course> getshortTimeCourse(long roomId);

    void setShareTime(long mustShareTime ,long courseId);

    /**
     * WHA
     * @param name 搜索使用字段 直播间名称
     * @param offset 分页参数
     * @param pageSize 分页参数
     * @return
     */
    ActResultDto findLiveRoomByName(String name ,Integer offset, Integer pageSize);

    /**
     * WHA
     * @param liveTopic 搜索使用字段 直播主题
     * @param offset  分页参数
     * @param pageSize  分页参数
     * @param   isSeriesCourse 1-是系列课0-是单节课
     * @return
     */
    ActResultDto findCourseByLiveTopic(String liveTopic ,Integer offset, Integer pageSize,String isSeriesCourse);

    /** 首页 搜索默认
     * WHA
     * @param condition 搜索使用字段 直播主题
     * @return
     */
    ActResultDto findCourseAndLiveRoomByCondition(String condition,String type);

    ActResultDto updateCanConnect(long courseId , String isCan);

    /**
     * WHA
     * 单节课OR系列课 每周精选
     * @param
     * @return
     */
    ActResultDto findCourseWeeklySelection();
    /**
     * WHA
     * 单节课OR系列课 精选课程
     * @param
     * @return
     */
    ActResultDto findCourseAllSelection(Integer offset, Integer pageSize);

    /**
     * 首页精彩推荐 综合接口
     * @return
     */
    ActResultDto wonderfulrecommendation();
    ActResultDto wonderfulrecommendationV164();
    ActResultDto getMyHistoryCourseList(DataGridPage page,Long appId,String type);
    ActResultDto getMyBuyFreeCourseList(DataGridPage page,Long appId,String clientType);
    ActResultDto getMyLivedCourseList(DataGridPage dg,Long appId,String clientType);

    /**
     * 免费专区
     * @param offset
     * @param pageSize
     * @param sort 合排序--0 人气排序--1 时间排序--2
     * @param sc 时间正序--0 时间倒序--1
     * @return
     */
    ActResultDto findFreeAdmissionCoursePage(Integer offset, Integer pageSize,String sort,String sc);

    /**
     * 每周精选--更多（分页）
     * @param timeStr 0-本周 1-上周 3-其他
     * @param offset
     * @param pageSize
     * @return
     */
    ActResultDto findWeeklySelectionPage(Integer offset, Integer pageSize,String timeStr);

    /**
     * 正在直播
     * @param offset
     * @param pageSize
     * @return
     */
    ActResultDto findBeingroadcastive(Integer offset, Integer pageSize);

    /**
     * 免费专区
     * @param offset
     * @param pageSize
     * @param sort 合排序--0 人气排序--1 时间排序--2 价格 --3
     * @param sc 时间正序--0 时间倒序--1
     * @return
     */
    ActResultDto findRankingListPage(Integer offset, Integer pageSize,String sort,String sc);

    /**
     * 未开播
     * @param offset
     * @param pageSize
     * @return
     */
    ActResultDto findCommend4HomeV4Page(Integer offset, Integer pageSize);

    /**
     * 系列课数量
     * @param roomId
     * @return
     */
    int getSerisCourseCount(long roomId);

    /**
     * 老师的课程数
     * @param teacherId
     * @return
     */
    int getCourseCountByAppId(long teacherId,String type);

    Course selectCourseMsgByChatRoomId(long chatRoomId);

    /**
     * 检验课程id在定制邀请卡中是否存在
     * @param courseId
     * @return
     */
    int findCourseIsExist(Long courseId);
    /**
     * 插入邀请卡模板
     * @param courseCard
     * @return
     */
    int insertCourseCard(CourseCard courseCard,HttpServletRequest request);

    /**
     * 更新邀请卡url
     * @param courseCard
     * @return
     */
    public int updateCourseCard(CourseCard courseCard,HttpServletRequest request);
    public String doPreviewCourseCard(CourseCard courseCard,HttpServletRequest request);
    /**
     * 查询邀请卡
     * @param courseId
     * @return
     */
    CourseCard findCardUrlByCourseId(@Param("courseId") Long courseId);

    ActResultDto getCoursesByType(Integer offset, Integer pageSize,String courseType);

    int deleteCourseCard(CourseCard courseCard, HttpServletRequest request);

    /**
     * 获取课程状态
     * @param id
     * @return
     */
    ActResultDto getCourseStatus(long id);

    /**
     * 获取课程老师学生各收益金额
      * @param id
     * @return
     */
    ActResultDto getCourseCardAmt(long id,long app_id);

    /**
     * 获取所有可以转播的课程
     * @param sc  排序  正序--0 , 倒序--1
     * @param sort   综合排序  时间--1,价格--2,分成比例--3
     * @param offset
     * @param pageSize
     * @return
     */
    List<Map> queryCanBroadcastCoursePage(String courseName,long appid,String sc,String sort, Integer offset, Integer pageSize);

    /**
     * 修改课程转播信息
     * @param courseId
     * @param isRelay
     * @param relayCharge
     * @param relayScale
     * @return
     */
    ActResultDto updateCourseRelayInfo(String courseId, String isRelay, String relayCharge, String relayScale);

    Course getRelayCourse(long idL);

    void updateIsOpenById(int i, long id);
}
