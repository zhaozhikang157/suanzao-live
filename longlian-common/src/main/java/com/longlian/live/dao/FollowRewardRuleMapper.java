package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.FollowRewardRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface FollowRewardRuleMapper {

    int insert(FollowRewardRule followReward);

    int update(FollowRewardRule followReward);

    List<FollowRewardRule> getListPage(@Param("page") DatagridRequestModel datagridRequestModel, @Param("followRewardRule") FollowRewardRule followRewardRule);

    int deleteById(@Param("id")long id,@Param("status")long status);

    FollowRewardRule findById(long id);

    List<FollowRewardRule> getList();

}