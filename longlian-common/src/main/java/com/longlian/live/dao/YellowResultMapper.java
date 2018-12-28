package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.YellowResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/3/16.
 */
@Mapper
public interface YellowResultMapper {

//    int deleteByPrimaryKey(Long id);
//
    int insert(YellowResult record);
//
//    int insertSelective(YellowResult record);
//
//    YellowResult selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(YellowResult record);
//
//    int updateByPrimaryKey(YellowResult record);

      List<Map> queryYellowResultByConditionPage(@Param(value = "page") DatagridRequestModel page,@Param(value="map") Map map);

}
