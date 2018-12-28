package com.longlian.live.newdao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.Func;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;
@org.apache.ibatis.annotations.Mapper
public interface FuncMapper extends Mapper<Func> {

    List<Map> getFuncListPage(@Param(value = "page") DatagridRequestModel page,@Param(value="map") Map map);
    int isExistFunc(@Param(value="funcCode") String funcCode);
    void deleteById(@Param(value = "id")Long id);
}