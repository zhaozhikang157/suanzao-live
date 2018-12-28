package com.longlian.console.service.impl;

import com.huaxin.util.EndDateParameteUtil;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.console.dao.AppUserCommentMapper;
import com.longlian.console.service.AppUserCommentService;
import com.longlian.dto.AppUserCommentDto;
import com.longlian.live.dao.AccountMapper;
import com.longlian.live.dao.LlAccountMapper;
import com.longlian.model.CommentRecord;
import com.longlian.token.ConsoleUserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by pangchao on 2017/1/23.
 */
@Service("appUserCommentService")
public class AppUserCommentServiceImpl implements AppUserCommentService {
    private static Logger log = LoggerFactory.getLogger(AppUserCommentServiceImpl.class);
    @Autowired
    AppUserCommentMapper appUserCommentMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    LlAccountMapper llAccountMapper;


    @Override
    public void insertLlAccountAndAccount(Long id) {
        accountMapper.addAppId(id);
        llAccountMapper.addAppId(id);
    }
}