package com.longlian.console.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.ShareChannel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ShareChannelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ShareChannel record);

    int insertSelective(ShareChannel record);

    ShareChannel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ShareChannel record);

    int updateByPrimaryKey(ShareChannel record);

    List<Map> getShareChannelListPage(@Param("page") DatagridRequestModel requestModel, @Param("map") Map map);

    void deleteByIds(@Param(value = "item") String ids);

    List<Map> getChannelRecordListPage(@Param("page") DatagridRequestModel requestModel, @Param("name") String name);

}