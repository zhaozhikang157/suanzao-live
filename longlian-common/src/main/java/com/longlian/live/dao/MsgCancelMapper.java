package com.longlian.live.dao;

import com.longlian.model.MsgCancel;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * Created by admin on 2017/12/5.
 */
@Mapper
public interface MsgCancelMapper {

    void insertMsgCancel(MsgCancel msgCancel);

    Set<String> findMsgCancel(long courseId);

}
