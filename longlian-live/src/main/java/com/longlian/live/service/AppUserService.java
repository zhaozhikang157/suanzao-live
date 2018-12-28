package com.longlian.live.service;

import com.huaxin.util.ActResult;
import com.huaxin.util.weixin.ParamesAPI.WeixinAppUser;
import com.longlian.dto.ActResultDto;
import com.longlian.model.AppUser;
import com.longlian.token.AppUserIdentity;
import org.apache.ibatis.annotations.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/2/8.
 */
public interface AppUserService {

    AppUser getById(long id);
    ActResultDto loginIn(AppUser appUser , String machineType)throws Exception;
    ActResultDto weixinLogin(WeixinAppUser weixinAppUser,long invitationAppId , String systemType)throws Exception;
    ActResultDto drawInvitationCard(String bgcolor , HttpServletRequest request, Long courseId ,Long roomId);
    ActResultDto updateUser(long appUserId ,String name)throws Exception;
    Map getUserInfo(long id);
    long userLiveRoomFollowCount(long id);
    ActResultDto getApplySms(String mobile ,String redisKey,String type);
    ActResultDto registerMobile(AppUserIdentity appUserIdentity , String mobile ,String checkCode);
    int updatePhone(long id,String photo)  throws Exception;
    ActResultDto teacherSendCode(String mobile);
    Boolean verificationCode(String mobile,String code,String redisKey);
    ActResultDto setPassword(String mobile,String password);
    void updateForAppUserById(long id , String cardNo, String realName );

    public  void updateYunxinToken(long id,String yunxinToken);

    public List<AppUser> getAllAppUser();

 /*   List<Map> getCourseListByAppId(long id,Integer pageNum,Integer pageSize,long roomId);*/
    long getAppIdByOpenid(String openid);
    ActResultDto getCourseListByAppId(Long appId,Integer pageNum,Integer pageSize,long roomId);

    ActResultDto bindingMobile(long appId,String mobile , String code , String password);

    List<Map> getSeriesListByAppId(Long appId,Integer pageNum,Integer pageSize,long roomId , boolean isHaveRecord,Integer isQueryRelay);

    public void updateLogUserName(long appUserId , String field, String value );

    public ActResultDto getWeixinLoginFormWebsite(AppUser appUser) throws Exception;

    AppUser getByUnionid(String unionId);

    ActResultDto loginInByMobile(AppUser user, String machineType) throws Exception;
    public Map updateFetchUserInfo(long id);
    public Map getNameAndPhoto(Long id);
    //更新用户模糊背景
    int updateUserBlurPhoto(Long id,String blurPhoto);

    /**
     * 指定老师个人邀请卡
     * @param modelUrl
     * @param request
     * @param courseId
     * @param loginAppId
     * @param type
     * @return
     */
    public String doInvitationPrivateCard(String modelUrl, HttpServletRequest request, Long courseId, String type);

    /**
     * 获取版本
     * @return
     */
    public Map<String,String> getSystemVision();
    /** 查询管理员用户是否存在 */
    int findSystemAdminByUserId(Long adminId);

    //根据设备序列号查询用户
    AppUser selectByTouristDeviceId(String deviceId);
    //游客登录
    ActResultDto touristLogin(AppUser appUser)throws Exception;
}
