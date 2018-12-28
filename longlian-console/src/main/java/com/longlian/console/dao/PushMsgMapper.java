package com.longlian.console.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.PushMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PushMsgMapper {
    int insert(PushMsg pushMsg);
    int update(PushMsg pushMsg);
    List<Map> getPushMsgListPage(@Param("page") DataGridPage dg);
    Map getPushMsgById(Long id);
    //获取H5推送记录
    List<PushMsg> getH5PushMsgListPage(@Param("page") DataGridPage dataGridPage, @Param("map") Map map);
    //获取H5推送记录数
    Map getH5PushMsgCount(@Param("map") Map map);
}