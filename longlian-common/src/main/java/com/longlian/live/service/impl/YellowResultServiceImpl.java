package com.longlian.live.service.impl;

import com.huaxin.util.db.DataSource;
import com.huaxin.util.db.DynamicDataSourceKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.live.dao.YellowResultMapper;
import com.longlian.live.service.YellowResultService;
import com.longlian.model.YellowResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/3/16.
 */
@Service("yellowResultService")
public class YellowResultServiceImpl implements YellowResultService{
    @Autowired
    YellowResultMapper yellowResultMapper;

    @Override
    @DataSource(value= DynamicDataSourceKey.DS_LOG)
    public List<Map> queryYellowResultByCondition(DatagridRequestModel dg, Map map){
       return yellowResultMapper.queryYellowResultByConditionPage(dg,map);

    }

    @DataSource(value = DynamicDataSourceKey.DS_LOG)
    @Transactional(rollbackFor = Exception.class , propagation = Propagation.REQUIRES_NEW)
    @Override
    public void insert(YellowResult record) {
        yellowResultMapper.insert(record);
    }

}
