package com.longlian.console.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.AppUserCommentDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/1/23.
 */
public interface AppUserCommentService {
    List<AppUserCommentDto> getPendingCommentListPage(DatagridRequestModel datagridRequestModel,AppUserCommentDto appUserComment);

    List<AppUserCommentDto> getInHandOrAlreadyHandCommentListPage(DatagridRequestModel datagridRequestModel, AppUserCommentDto appUserComment);

    void setHandComment(HttpServletRequest request,AppUserCommentDto appUserComment);

    List<Map> gethandStatusList(String handStatus);

    Map getCommentByCommentId(long id);

    long getPendingHandle();

    long getAlreadyHandle();

    AppUserCommentDto getAppUserCommentById(long id);

    void insertLlAccountAndAccount(Long id);
}
