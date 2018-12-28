package com.longlian.live.service;


import com.longlian.model.AppUserComment;

/**
 * Created by U on 2016/8/17.
 */
public interface AppUserCommentService {
    void createAppUserComment(AppUserComment appUserComment);
    void insert(AppUserComment appUserComment)throws  Exception;
}
