package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.SystemPara;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by U on 2016/5/5.
 */
@Mapper
public interface SystemParaMapper {

    List<SystemPara> getListPage(@Param(value = "page") DatagridRequestModel datagridRequestModel, @Param(value = "systemPara") SystemPara systemPara);

    List<Map<String , String>>  getAllList();

    SystemPara selectById(Long id);

    String getCourseDivideScale(@Param(value = "code") String code);

    void create(SystemPara systemPara);

    void update(SystemPara systemPara);

    void deleteById(@Param(value = "ids")String ids);

    SystemPara selectByCode(@Param("code")String code);
}
