package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.longlian.model.PushMsg;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/17.
 */
public interface PushMsgService {

    List<Map> getPushMsgList(Integer pageNum,Integer pageSize);
    Map getPushMsgById(Long id);
}
