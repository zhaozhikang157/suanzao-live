package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcelWhaUtil;
import com.longlian.dto.InviCodeItemDto;
import com.longlian.model.InviCodeItem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/29.
 */
public interface InviCodeItemService {

    List<InviCodeItemDto> getInviCodeItemPage(DataGridPage page , long id);

    ExportExcelWhaUtil importExcelInviCodeItem(HttpServletRequest request, HttpServletResponse response ,Long id);

    void insertItem(long inviCodeId , int num , long courseId);

    List<Long> getNoUseInviCode(long id);

    InviCodeItem getItemInfo(long itemId);

    void updateUseAppId(long useId,long id ,Date time , long inviCodeId);

    void updateUseAppIdTwo(long useId,long id ,Date time , long inviCodeId);

    InviCodeItem getItemInfoByInviCode(String inviCode , Long inviCodeId);

    void updateUseTime(InviCodeItem item);

    Map getItemInfoByInviCodeAndcourseId(String inviCode , Long coureId);

    String findCourseName(long itemId);
}
