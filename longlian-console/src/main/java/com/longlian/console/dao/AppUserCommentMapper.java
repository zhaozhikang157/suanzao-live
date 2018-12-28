package com.longlian.console.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.AppUserCommentDto;
import com.longlian.model.CommentRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by pangchao on 2017/1/23.
 */
public interface AppUserCommentMapper {

    List<AppUserCommentDto> getPendingCommentListPage(@Param("page")DatagridRequestModel datagridRequestModel, @Param("appUserComment")AppUserCommentDto appUserComment);

    List<AppUserCommentDto> getInHandOrAlreadyHandCommentListPage(@Param("page")DatagridRequestModel datagridRequestModel,@Param("appUserComment")AppUserCommentDto appUserComment);

    void setHandComment(@Param("appUserComment")AppUserCommentDto appUserComment);

    void createCommentRecord(@Param("commentRecord")CommentRecord commentRecord);

    List<CommentRecord> getCommentByCommentId(@Param("commentId")long id);

    long getPendingHandle();

    long getAlreadyHandle();

    AppUserCommentDto getAppUserCommentById(long id);
}
