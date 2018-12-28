package com.longlian.console.service;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.model.AppUser;
import com.longlian.model.system.SystemAdmin;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/14.
 */
public interface AppUserService {

    DatagridResponseModel getAllAppUserinfoPage(String name , String mobile ,
                                    String tokenType , DatagridRequestModel requestModel);


    List<Map> getInvitationTeachRewardList();

    Map getAppUsers(long id);

    /**
     * 会员钱包-提现记录
     * @param id
     * @return
     */
    Map withdrawDeposit(long id);

    Map membershiprebate(long id,Map map);

    List<Map> getMemberDetailsList(DatagridRequestModel page,Map map);
    List<Map> getTeacherListPage(DatagridRequestModel page,Map map);

    AppUser getAppUserById(long id);

    List<Long> findAllAppUser();

    int getUserCount();

    List<Map> findNoYunxinTokenUser();


    void resetPwd(Long id,String password);

    ActResult updateProportion(Long id,Integer addCount);

    /**
     * 查询超级管理员
     * @return
     */
    List<SystemAdmin> getSystemAdminList();
    int insertSystemAdmin(SystemAdmin admin) throws Exception;
    int deleteSystemAdmin(Long id);
}
