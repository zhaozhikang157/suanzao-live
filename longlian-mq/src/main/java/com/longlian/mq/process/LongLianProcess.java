package com.longlian.mq.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liuhan on 2017-06-07.
 */
public abstract class LongLianProcess implements InitializingBean {
    private Logger log = LoggerFactory.getLogger(LongLianProcess.class);

    protected boolean isRun = true;

    protected static boolean isAllColse = false;

    public ExecutorService threadPool = null;
    /**
     * 设置isSysMsgRun=false关闭 isSysMsgRun=true开启
     * @param isRun
     * @throws Exception
     */
    public void setRun(boolean isRun) throws Exception {
        //如果之前是关闭且现在是要开启
        if (!this.isRun && isRun) {
            //开启
            this.isRun = isRun;

            afterPropertiesSet();
        } else {
            this.isRun = isRun;
        }
    }

    public static void setIsAllColse(boolean isAllColse) {
        LongLianProcess.isAllColse = isAllColse;
    }

    public abstract void addThread();

    public abstract int getThreadCount();

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("{},正在初史化线程数:{}",this.getClass().getName() ,  this.getThreadCount());
        threadPool = Executors.newFixedThreadPool(this.getThreadCount());
        for(int i=0;i< this.getThreadCount();i++){
            addThread();
        }
        log.info("{},完成初史化线程数:{}",this.getClass().getName() ,  this.getThreadCount());
    }
}
