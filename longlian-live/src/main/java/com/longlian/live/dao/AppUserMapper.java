package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.AppUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/8.
 */
public interface AppUserMapper {

    AppUser selectByPrimaryKey(long id);
    AppUser queryByOpenid(String openid);
    AppUser queryByUnionid(String unionid);
    long getAppIdByOpenid(String openid);
    List<AppUser> getAllUser();

    void  insert(AppUser appUser);
    int updateUser(@Param("id")long appUserId ,@Param("name")String name);
    int updatePhone(@Param("id")long id ,@Param("photo")String photo);
    int updateMobile(@Param("id")long id ,@Param("mobile")String mobile);
    int updateWechatInfo(@Param("id")long id ,@Param("photo")String photo , @Param("name")String name ,  @Param("gender")String gender
            , @Param("unionid") String unionid  , @Param("openid") String openid ,  @Param("isFollowLlWechat") String isFollowLlWechat);
    int update(AppUser appUser);
    int updateYunXinToken(@Param("id")long id ,@Param("token")String token );
    void setPassword(@Param("mobile")String mobile , @Param("password")String password);
    void updateForAppUserById(@Param("id")long id ,@Param("map")Map map);
    List<Map> getCourseListByAppIdPage(@Param("page")DataGridPage dg,@Param("id")long id,@Param("roomId")long roomId);

    void updateMobileAndPassword(@Param("mobile")String mobile,@Param("password")String password,@Param("id")long appId);

    List<Map> getSeriesCourseListByAppIdPage(@Param("page")DataGridPage dg,@Param("roomId")long roomId ,  @Param("isRecorded") String isRecorded
                                            , @Param("isQueryRelay")Integer isQueryRelay);
    List<Map> getSeriesCourseListByOtherAppIdPage(@Param("page")DataGridPage dg,@Param("roomId")long roomId ,  @Param("isRecorded") String isRecorded
                                               , @Param("isQueryRelay")Integer isQueryRelay);
    //更新用户模糊背景
    int updateUserBlurPhoto(@Param("id") Long id,@Param("blurPhoto") String blurPhoto);
    //获得用户模糊背景图
    Map getNameAndPhoto(@Param("id") Long id);

    Map<String,String> getSystemVision();
    //根据设备序列号查询用户
    AppUser selectByTouristDeviceId(@Param("deviceId")String deviceId);

    List<Map> getSeriesCoursePage(@Param("page")DataGridPage dg,@Param("id")long id,@Param("roomId")long roomId);
}
