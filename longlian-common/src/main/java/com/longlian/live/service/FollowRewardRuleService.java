package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.FollowRewardRule;

import java.util.List;

/**
 * Created by pangchao on 2017/2/25.
 */
public interface FollowRewardRuleService {

    List<FollowRewardRule> getListPage(DatagridRequestModel datagridRequestModel, FollowRewardRule followRewardRule);

    void deleteById(long id,long status) throws Exception;

    List getFollowRewardRule4Redis();

    FollowRewardRule findById(long id);

    void  doSaveAndUpdate( FollowRewardRule followRewardRule);
}
