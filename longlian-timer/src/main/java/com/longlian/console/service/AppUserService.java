package com.longlian.console.service;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.model.AppUser;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/14.
 */
public interface AppUserService {

    DatagridResponseModel getAllAppUserinfoPage(String name, String mobile,
                                                String tokenType, DatagridRequestModel requestModel);


    List<Map> getInvitationTeachRewardList();

    Map getAppUsers(long id);

    /**
     * 会员钱包-提现记录
     * @param id
     * @return
     */
    Map withdrawDeposit(long id);

    List<Map> getMemberDetailsList(DatagridRequestModel page, Map map);
    List<Map> getTeacherListPage(DatagridRequestModel page, Map map);

    AppUser getAppUserById(long id);

    List<Long> findAllAppUser();

    int getUserCount();

    List<Map> findNoYunxinTokenUser();


    void resetPwd(Long id, String password);
    /**
     * 查询绑定直播间的服务号对应用户手机号及过期时间
     * @return
     */
    List<Map> findUserPhoneList();
}
