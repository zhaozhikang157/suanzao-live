package com.longlian.live.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.dto.InviCodeItemDto;
import com.longlian.model.InviCodeItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/29.
 */
@Mapper
public interface InviCodeItemMapper {

    List<InviCodeItemDto> getInviCodeItemPage(@Param("page")DataGridPage page,@Param("inviCodeId")long inviCodeId);

    List<Map> getAllInviCodeItem(long inviCodeId);

    void insertCode(@Param("inviCodeId")long inviCodeId , @Param("list")List<String> inviCode);

    List<Long> getNoUseInviCode(long inviCodeId);

    InviCodeItem getItemInfo(long id);

    void  updateUseAppId(@Param("useId")long useId,@Param("id")long id ,@Param("useTime")Date date);

    void  updateUseTime(long id);

    InviCodeItem getItemInfoByInviCode(@Param("inviCode")String inviCode , @Param("inviCodeId")long inviCodeId);


    Map getItemInfoByInviCodeAndCourseId(@Param("inviCode")String inviCode , @Param("courseId")long courseId);

    String findCourseName(long itemId);
}
