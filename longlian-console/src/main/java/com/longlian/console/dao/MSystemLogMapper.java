package com.longlian.console.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.MSystemLogDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by pangchao on 2017/1/22.
 */
public interface MSystemLogMapper {
    /**
     * 全查
     *
     * @param mSystemLog
     * @return
     */
    List<MSystemLogDto> getListPage(@Param(value = "page") DatagridRequestModel page, @Param(value = "mSystemLog") MSystemLogDto mSystemLog);
}
