package com.longlian.mq.process;

import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.EmailUtil;
import com.huaxin.util.IPUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.model.Course;
import com.longlian.type.YunxinCustomMsgType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuhan on 2017-06-07.
 */
public abstract class DataRunner implements Runnable {

    public RedisUtil redisUtil;
    //邮件发送次数
    public static int sendTime = 0;

    public String redisKey;

    public LongLianProcess longLianProcess;


    private static Logger logg = LoggerFactory.getLogger(DataRunner.class);

    public DataRunner( LongLianProcess longLianProcess , RedisUtil redisUtil  , String redisKey) {
        this.redisUtil = redisUtil;
        this.redisKey = redisKey;
        this.longLianProcess = longLianProcess;
    }

    public DataRunner( LongLianProcess longLianProcess, RedisUtil redisUtil) {
        this.longLianProcess = longLianProcess;
        this.redisUtil = redisUtil;
    }

    public String getMsg() {
       return redisUtil.rpop(redisKey);
    }

    @Override
    public void run() {
        //不是所有关闭
        while(!LongLianProcess.isAllColse && longLianProcess.isRun){
            String msg = null;
            try{
                 msg = getMsg();
            }catch(Exception e){
                //redis报错发送邮件
                logg.error("{},redis接收消息报错:{}", this.getClass().getName(),e);
                if (sendTime < 10) {
                    GlobalExceptionHandler.sendEmail(e ,  "mq重要错误");
                    sendTime++;
                }
            }
            if(StringUtils.isEmpty(msg)){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                logg.info(msg);
                try{
                    process(msg);
                }catch(Exception e){
                    logg.error("{},处理报错:{}", this.getClass().getName(),e);
                    GlobalExceptionHandler.sendEmail(e , "mq错误");
                }
            }
        }
        logg.info( "类{} ：线程 ：{}已经关闭" , this.getClass().getName(),Thread.currentThread().getName());
    }

    public abstract void process(String msg) throws Exception;


}
