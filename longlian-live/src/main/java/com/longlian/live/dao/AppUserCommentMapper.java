package com.longlian.live.dao;


import com.longlian.model.AppUserComment;

/**
 * Created by U on 2016/8/17.
 */
public interface AppUserCommentMapper {

    void createAppUserComment(AppUserComment appUserComment);
    int insert(AppUserComment appUserComment);
}
