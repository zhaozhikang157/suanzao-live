package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.LiveChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface LiveChannelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LiveChannel record);

    int insertSelective(LiveChannel record);

    LiveChannel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LiveChannel record);

    int updateByPrimaryKey(LiveChannel record);

    List<LiveChannel> getAll();

    List<LiveChannel> getListPage(@Param("page")DatagridRequestModel datagridRequestModel, @Param("liveChannel")LiveChannel liveChannel);

    long getByCode(@Param("code")String code,@Param("id")long id);
}