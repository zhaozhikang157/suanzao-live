package com.longlian.live.service.impl;

import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.service.LlAccountService;
import com.longlian.live.system.AppUserAccountDataRunner;
import com.longlian.model.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by syl on 2017-07-10.
 */
@Service
public  class AppUserAccountDataRunnerImpl {
    private static Logger logg = LoggerFactory.getLogger(AppUserAccountDataRunnerImpl.class);
    //开启100个线程执行创建钱包账户和学币账号
    ExecutorService threadPool  = Executors.newFixedThreadPool(100);
    @Autowired
    AppUserCommonService appUserCommonService;


    private class GetData extends AppUserAccountDataRunner {

        @Override
        public void setData(AppUser appUser) {
            super.setData(appUser);
        }

        @Override
        public void process(AppUser appUser)  {
            appUserCommonService.handleAccount(appUser);
        }
    }


    /**
     * 处理，添加到线程池中
     * @param appUser
     * @throws Exception
     */
    public void headler (AppUser appUser) throws Exception{
        GetData data = new GetData();
        data.setData(appUser);
        threadPool.execute(data);
    }

}
