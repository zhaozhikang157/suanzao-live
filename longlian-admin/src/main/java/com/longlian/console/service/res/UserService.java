package com.longlian.console.service.res;

import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.model.MRes;
import com.longlian.model.MUser;

import java.util.List;

public interface UserService {
    /**
     * 查看所有的员工
     * @param page
     * @param mUser
     * @return
     */
    DatagridResponseModel getListPage(DatagridRequestModel page, MUser mUser);

    void createOrUpdate(MUser mUser);

    List<MRes> getAllRole(String type);

    MUser findById(long id);

    void deleteByIds(String ids, String status);

    void passwordReset(String ids);

    MUser findUserId(String userId);

    /**
     * 根据当前登录人来修改密码
     * @param userId
     */
    void updatePassWord(long userId, String passWord);

    boolean checkPassword(MUser user, String password) throws Exception;

    public String createToken(long userid);
}
