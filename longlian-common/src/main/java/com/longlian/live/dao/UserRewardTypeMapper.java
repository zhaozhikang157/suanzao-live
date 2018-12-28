package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.UserRewardType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by syl on 2017/5/22.
 */
@Mapper
public interface UserRewardTypeMapper {
    List<UserRewardType> getUseList(String status);

    UserRewardType getById(long id);

    List<UserRewardType> findrewardTypeInfoPage(@Param("page")DataGridPage dataGridPage ,
                                                @Param("userRewardType")UserRewardType userRewardType);

    void createUserRewardType(UserRewardType userRewardType);

    void updateUserRewardType(UserRewardType userRewardType);

    void updateStatus(@Param("id")long id , @Param("status")String status);

}
