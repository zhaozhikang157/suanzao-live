package com.longlian.console.dao;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.AppUserDto;
import com.longlian.model.AppUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/14.
 */
public interface AppUserMapper {

    Map getTrackUserInfo(@Param("map")Map map);
    List<Map> getAllAppUserinfoPage(@Param("page") DatagridRequestModel requestModel,@Param("name")String name ,
                                    @Param("mobile")String mobile ,@Param("userType") String userType);

    List<Map> getInvitationTeachRewardList(@Param("ids") String ids);

    Map getAppUser(long id);

    List<Map> findBankCard(long id);

    List<Map> getMemberDetailsListPage(@Param("page")DatagridRequestModel page, @Param("map")Map map);

    List<Map> getTeacherListPage(@Param("page")DatagridRequestModel page, @Param("map")Map map);

    AppUser findById(long appId);

    List<Long> findAllAppUser();

    void importUser(List<AppUser> list);

    List<Long> findAvatars();

    List<AppUserDto> findAllAvatarPage(@Param("page")DataGridPage page
            , @Param("name")  String name
            , @Param("isInRoom")  String isInRoom
            , @Param("courseId") Long courseId);
    Map findAllAvatarTotalCount( @Param("name")  String name
            , @Param("isInRoom")  String isInRoom
            , @Param("courseId") Long courseId);
    List<AppUserDto> findAllAvatar();

    void updateYunxinToken(@Param("id")  long id,@Param("token")  String token);

    List<AppUser> getUsersByCount(@Param("courseId") Long courseId,@Param("count") Long count);

    void updateAvatar(AppUser appUser);

    int getUserCount();

    List<Map> findNoYunxinTokenUser();

    void resetPwd( @Param("id") Long id, @Param("password") String password);

    void updateProportion( @Param("id") Long id, @Param("addCount") Integer addCount);

}
