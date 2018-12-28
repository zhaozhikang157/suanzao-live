package com.huaxin.util.redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Created by han on 2017/7/9.
 */
@Component("redisLock")
public class RedisLock {
    private static final long MILLI_NANO_TIME = 1000000l;
    @Autowired
    private RedisUtil redisUtil;
    private static Logger log = LoggerFactory.getLogger(RedisLock.class);
    /**
     * 加锁
     * 使用方式为：
     * lock();
     * try{
     *    executeMethod();
     * }finally{
     *   unlock();
     * }
     * @param timeout timeout的时间范围内轮询锁
     * @param expire 设置锁超时时间
     * @return 成功 or 失败
     */
    public boolean lock(String key , long timeout,int expire){
        long nanoTime = System.nanoTime();
        timeout *= MILLI_NANO_TIME;
        Random random = new  Random();
        try {
            //在timeout的时间范围内不断轮询锁
            while (System.nanoTime() - nanoTime < timeout) {
                //锁不存在的话，设置锁并设置锁过期时间，即加锁

                if (redisUtil.setnx(key, String.valueOf(System.currentTimeMillis()) +"_" + timeout) == 1) {
                    redisUtil.expire(key, expire);//设置锁过期时间是为了在没有释放
                    //锁的情况下锁过期后消失，不会造成永久阻塞
                    return true;
                }
                //System.out.println("出现锁等待");
                //log.info("出现锁{}等待", key);
                //短暂休眠，避免可能的活锁
                Thread.sleep(3, random.nextInt(32));
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 试图加锁,加锁失败返回false
     * @param key
     * @param timeout
     * @param expire
     * @return
     */
    public boolean haslock(String key , long timeout,int expire){
        try {
            //锁不存在的话，设置锁并设置锁过期时间，即加锁
            if (redisUtil.setnx(key, String.valueOf(System.currentTimeMillis()) +"_" + timeout) == 1) {
                redisUtil.expire(key, expire);//设置锁过期时间是为了在没有释放
                //锁的情况下锁过期后消失，不会造成永久阻塞
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public  void unlock(String key) {
        redisUtil.del(key);
    }
}
