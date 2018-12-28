package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.Func;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/11/21.
 */
public interface FuncService {
    List<Map> getFuncListPage(DatagridRequestModel datagridRequestModel,Map map);
    Func findModelById(Long id);
    void  doSaveAndUpdate(Func func) throws Exception;
    boolean isExistFunc(String funcCode);
    void deleteById(Long id) throws Exception;
}
