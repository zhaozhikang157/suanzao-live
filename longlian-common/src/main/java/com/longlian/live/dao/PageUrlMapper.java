package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.PageUrl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface PageUrlMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PageUrl record);

    int insertSelective(PageUrl record);

    PageUrl selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PageUrl record);

    int updateByPrimaryKey(PageUrl record);
    List<PageUrl> selectAll();
    
    List<Map> getPageUrlListPage(@Param("page")DatagridRequestModel requestModel,@Param("map")Map map);
    PageUrl selectByUrl(@Param("pageUrl") String url);
}