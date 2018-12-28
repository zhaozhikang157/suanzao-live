package com.longlian.live.dao;


import com.longlian.model.StoreFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StoreFileMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StoreFile record);

    StoreFile selectByPrimaryKey(Long id);
 
}