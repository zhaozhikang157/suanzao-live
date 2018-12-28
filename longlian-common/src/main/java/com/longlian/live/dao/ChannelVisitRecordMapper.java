package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.ChannelVisitRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface ChannelVisitRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ChannelVisitRecord record);

    int insertSelective(ChannelVisitRecord record);

    ChannelVisitRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ChannelVisitRecord record);

    int updateByPrimaryKey(ChannelVisitRecord record);

    ChannelVisitRecord selectByChannelVisitRecord(ChannelVisitRecord channelVisitRecord);
    

    long getNewChannelVisit(@Param("channelId")Long channelId,@Param("courseId")Long courseId);
    long getAllChannelVisit(@Param("channelId")Long channelId,@Param("courseId")Long courseId);

    List<ChannelVisitRecord> selectByCourse(@Param("courseId")Long courseId );
}