package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.dto.InviCodeDto;
import com.longlian.model.InviCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/29.
 */
@Mapper
public interface InviCodeMapper {

    List<InviCodeDto> getAllInviCodePage(@Param("page")DataGridPage page ,
                                         @Param("appId")long appId,@Param("inviCode")InviCodeDto inviCode);

    void insertInviCode(InviCode inviCode);

    List<Map> getAllInviCode(@Param("appId")Long appId,@Param("courseName")String courseName , @Param("id")Long id);

    InviCodeDto findCodeInfo(long id);

    InviCode getCodeInfo(long id);

    void updateBalanceCount(long id);

    Map getInfo(long id);

}
