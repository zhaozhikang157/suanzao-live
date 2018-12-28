package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.YellowResult;

import java.util.List;
import java.util.Map;

/**
 *
 * 鉴黄结果保存
 * Created by admin on 2017/3/16.
 */
public interface YellowResultService {
    List<Map> queryYellowResultByCondition( DatagridRequestModel page, Map map);

    public void insert(YellowResult record);
}
