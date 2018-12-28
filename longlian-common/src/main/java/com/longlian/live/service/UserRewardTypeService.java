package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.longlian.model.UserRewardType;

import java.util.List;

/**
 * Created by syl on 2017/4/19.
 */
public interface UserRewardTypeService {
    List<UserRewardType> getUseList(String status);
    UserRewardType getById(long id);
    List<UserRewardType> findrewardTypeInfoPage(DataGridPage page , UserRewardType userRewardType);
    void createUserRewardType(UserRewardType userRewardType);
    void updateUserRewardType(UserRewardType userRewardType);
    void updateStatus(long id ,  String status);
}
