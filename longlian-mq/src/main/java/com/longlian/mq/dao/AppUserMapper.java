package com.longlian.mq.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.AppUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/8.
 */
public interface AppUserMapper {
    List<AppUser> getNames(List<Long> ids);

    String getOpenidById(long id);

    Map getNameAndPhoto(@Param("id") Long id);

    AppUser selectByPrimaryKey(long id);
    //更新用户模糊背景
    int updateUserBlurPhoto(@Param("id") Long id,@Param("blurPhoto") String blurPhoto);
}
