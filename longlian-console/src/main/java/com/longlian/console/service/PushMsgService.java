package com.longlian.console.service;

import com.huaxin.util.DataGridPage;
import com.longlian.model.PushMsg;

import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/17.
 */
public interface PushMsgService {

    List<Map> getPushMsgList(Integer pageNum, Integer pageSize);
    Map getPushMsgById(Long id);
    //获取H5推送记录
    DataGridPage getH5PushMsgListPage(DataGridPage dataGridPage, Map map) throws Exception;
    //插入H5推送信息
    int insertH5PushMsg(PushMsg PushMsg) throws Exception;
}
