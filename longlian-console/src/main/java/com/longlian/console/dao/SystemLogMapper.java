package com.longlian.console.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.SystemLogDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by pangchao on 2017/1/22.
 */
public interface SystemLogMapper {

    List<SystemLogDto> getListPage(@Param("page")DatagridRequestModel datagridRequestModel, @Param("systemLogDto")SystemLogDto systemLogDto);

    List<SystemLogDto> getListByType(@Param("type") String type);
}
