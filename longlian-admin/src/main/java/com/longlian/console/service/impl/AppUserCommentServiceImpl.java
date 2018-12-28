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


   /**
     * 获取待处理数据
     * @return
     */
    @Override
    public List<AppUserCommentDto> getPendingCommentListPage(DatagridRequestModel datagridRequestModel,AppUserCommentDto appUserComment) {
        appUserComment.setEndnTime(EndDateParameteUtil.parserEndDate(appUserComment.getEndnTime()));
        return appUserCommentMapper.getPendingCommentListPage(datagridRequestModel,appUserComment);
    }

    /**
     * 获取处理中或者已处理的数据
     * @param datagridRequestModel appUserComment
     * @return
     */
    @Override
    public List<AppUserCommentDto> getInHandOrAlreadyHandCommentListPage(DatagridRequestModel datagridRequestModel, AppUserCommentDto appUserComment){
        appUserComment.setEndnTime(EndDateParameteUtil.parserEndDate(appUserComment.getEndnTime()));
        return appUserCommentMapper.getInHandOrAlreadyHandCommentListPage(datagridRequestModel, appUserComment);
    }


    /**
     * 设置处理反馈意见
     * @param appUserComment
     * @return
     */
    @Override
    public void setHandComment(HttpServletRequest request,AppUserCommentDto appUserComment) {
        ConsoleUserIdentity token = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        CommentRecord commentRecord=new CommentRecord();
        commentRecord.setHandUserId(token.getId());
        commentRecord.setCommentId(appUserComment.getId());
        commentRecord.setHandStatus(appUserComment.getHandStatus());
        commentRecord.setRemarks(appUserComment.getHandRemarks());
        commentRecord.setCreateTime(new Date());
        commentRecord.setCourseId(appUserComment.getCourseId());
        appUserCommentMapper.createCommentRecord(commentRecord);   //创建处理反馈记录
        appUserCommentMapper.setHandComment(appUserComment);        //设置意见反馈处理状态
    }

    /**
     * 获取处理状态
     * @param handStatus
     * @return
     */
    @Override
    public List<Map> gethandStatusList(String handStatus) {
        List<Map> list=new ArrayList<Map>();
        if(handStatus.equals("0")){         //当前为待处理状态
            Map map=new HashMap();
            map.put("value" , "1");
            map.put("text" , "处理中");
            Map map2=new HashMap();
            map2.put("value" , "2");
            map2.put("text" , "处理");
            list.add(map);
            list.add(map2);
        }else if(handStatus.equals("1")){  //当前为处理中状态
            Map map=new HashMap();
            map.put("value" , "2");
            map.put("text" , "处理");
            list.add(map);
        }
        return list;
    }

    @Override
    public Map getCommentByCommentId(long id) {
        Map map=new HashMap();
        List<CommentRecord> list=appUserCommentMapper.getCommentByCommentId(id);
        if (list.size()>0){
            for (CommentRecord c : list){
                if ("1".equals(c.getHandStatus())){
                    map.put("remarks0",c.getRemarks());
                }else{
                    map.put("remarks1",c.getRemarks());
                }
            }
        }
        return map;
    }

    @Override
    public long getPendingHandle() {
        return appUserCommentMapper.getPendingHandle();
    }

    @Override
    public long getAlreadyHandle() {
        return appUserCommentMapper.getAlreadyHandle();
    }

    @Override
    public AppUserCommentDto getAppUserCommentById(long id) {
        return appUserCommentMapper.getAppUserCommentById(id);
    }

    @Override
    public void insertLlAccountAndAccount(Long id) {
        accountMapper.addAppId(id);
        llAccountMapper.addAppId(id);
    }
}