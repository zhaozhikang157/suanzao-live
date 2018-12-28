package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.model.Course;
import com.longlian.model.MsgCancel;

import java.util.Set;

/**
 * Created by admin on 2017/12/5.
 */
public interface MsgCancelService {

    ActResultDto insertMsgCancel(MsgCancel msgCancel,Course course);

    void insertMsg(MsgCancel msgCancel);

    String findMsgCancel(Long courseId);
}
