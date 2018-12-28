package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.CdnVisit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/10/12.
 */
@Mapper
public interface CdnVisitMapper {

    void insertCdnVisit(CdnVisit cdnVisit);

    List<Map> getSizeByRoomId();

    List<Map> getAllCourseFlowPage(@Param("page")DataGridPage page , @Param("roomId")long roomId);
}
