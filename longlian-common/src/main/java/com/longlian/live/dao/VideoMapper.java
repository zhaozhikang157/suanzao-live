package com.longlian.live.dao;

import com.longlian.model.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface VideoMapper {
    List<Video> findByUrl(@Param(value = "address") String fileURL);
    /**
     * 修改
     * @param record
     */
    void updateByPrimaryKeySelective(Video record);

    void insert(Video video);

    List<Video> getAllNoConvertVideo();

    void updateVideoAddress(@Param("id") Long id,@Param("videoAddress") String videoAddress);
}
