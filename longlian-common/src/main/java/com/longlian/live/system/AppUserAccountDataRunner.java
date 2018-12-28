package com.longlian.live.system;

import com.longlian.model.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by syl on 2017-07-10.
 */

public  abstract  class AppUserAccountDataRunner implements Runnable {
    private static Logger logg = LoggerFactory.getLogger(AppUserAccountDataRunner.class);
    //邮件发送次数
    public static int sendTime = 0;
    public AppUser appUser;
    public void setData(AppUser appUser){
        this.appUser = appUser;
    }
    @Override
    public void run() {
        if(appUser != null  ){
            process(appUser);
        }
    }

    public  abstract  void process( AppUser appUser);

}
