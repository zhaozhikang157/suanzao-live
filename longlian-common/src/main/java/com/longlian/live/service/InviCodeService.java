package com.longlian.live.service;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExportExcelWhaUtil;
import com.longlian.dto.InviCodeDto;
import com.longlian.model.Course;
import com.longlian.model.InviCode;
import com.longlian.token.AppUserIdentity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/29.
 */
public interface InviCodeService {

    List<InviCodeDto> getAllInviCodePage(DataGridPage dataGridPage , long appId , InviCodeDto inviCode);

    void insertInviCode(InviCode inviCode);

    ExportExcelWhaUtil importExcelInviCode(HttpServletRequest request , HttpServletResponse response , String courseName , Long id);

    InviCodeDto findInviCodeInfo(long id);

    InviCode findInviCode(long id);

    Map getInfo(long id);

    String isUseTime(String inviCodeId , long appId);

    Map getInviCode(long courseId , AppUserIdentity identity , Long inviCodeId , Course course) throws Exception;

}
