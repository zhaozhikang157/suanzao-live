package com.longlian.console.timer;

import com.huaxin.util.Utility;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.AppUserCommentService;
import com.longlian.console.service.AppUserService;
import com.longlian.live.service.LlAccountService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.model.Course;
import com.longlian.type.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/2/23.
 * 循环处理 查询AppUser 和 LLAccount 的ID 是相同
 */
@Component("findUserAndLLAccountTimer")
public class FindUserAndLLAccountTimer extends AbstractShardingTask {

    private static Logger log = LoggerFactory.getLogger(FindUserAndLLAccountTimer.class);

    @Autowired
    AppUserService appUserService;
    @Autowired
    LlAccountService llAccountService;
    @Autowired
    AppUserCommentService appUserCommentService;

    /**
     * 任务名称
     * @return
     */
    @Override
    public String getTaskName() {
        return "循环处理 查询AppUser 和 LLAccount 的ID 是相同";
    }

    /**
     * 具体执行的任务
     */
    @Override
    public void doExecute() {
        try{
            job();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("循环处理 查询AppUser 和 LLAccount 的ID 是相同 异常："+e.getMessage());
        }
    }

    public void job() throws  Exception {
        int userCount = appUserService.getUserCount();
        int llAccountCount = llAccountService.getLlAccountCount();
        if(userCount!=llAccountCount){
            List<Long> userIds = appUserService.findAllAppUser();
            List<Long> llAccountIds = llAccountService.findAllLlAccount();
            userIds.removeAll(llAccountIds);
            Thread.sleep(5000);
            for(Long id : userIds){
                appUserCommentService.insertLlAccountAndAccount(id);
            }
        }
    }


}
