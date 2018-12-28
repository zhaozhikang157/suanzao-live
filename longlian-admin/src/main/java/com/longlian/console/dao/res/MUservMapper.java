package com.longlian.console.dao.res;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.MUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MUservMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MUser record);

    int insertSelective(MUser record);

    MUser selectByPrimaryKey(Long id);
    /**
     * 
     * @param userId
     * @param onlyCanLogin 是否只查能登录的人员
     * @return
     */
    MUser selectByUserId(@Param("userId") String userId, @Param("onlyCanLogin") boolean onlyCanLogin);

    int updateByPrimaryKeySelective(MUser record);

    int updateByPrimaryKey(MUser record);

    List<MUser> getListPage(@Param("page") DatagridRequestModel page, @Param("mUser") MUser mUser);

    void deleteByIds(@Param("item") String ids, @Param("status") String status);

    void passwordReset(@Param("item") String ids, @Param("password") String password);

    MUser findUserId(String userId);


    List<MUser> getMUserByRole(@Param("roleId") Long roleId);

}
