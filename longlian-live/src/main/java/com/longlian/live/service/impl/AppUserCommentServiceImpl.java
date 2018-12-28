package com.longlian.live.service.impl;

import com.longlian.live.dao.AppUserCommentMapper;
import com.longlian.live.service.AppUserCommentService;
import com.longlian.model.AppUserComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by U on 2016/8/17.
 */
@Service("appUserCommentService")
public class AppUserCommentServiceImpl implements AppUserCommentService {

    @Autowired
    AppUserCommentMapper appUserCommentMapper;


    @Override
    public void createAppUserComment(AppUserComment appUserComment) {
        appUserCommentMapper.createAppUserComment(appUserComment);
    }

    @Override
    public void insert(AppUserComment appUserComment)throws  Exception {
        appUserCommentMapper.insert(appUserComment);
    }
}
