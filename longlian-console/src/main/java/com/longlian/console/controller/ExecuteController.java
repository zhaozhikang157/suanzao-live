package com.longlian.console.controller;

import com.huaxin.util.ActResult;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.CourseDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuhan on 2017-06-24.
 */
@Controller
@RequestMapping("/exc")
public class ExecuteController {
    @Autowired
    RedisUtil redisUtil;
    /**
     * 执行任务
     *
     * @return
     */
    @RequestMapping("/expireTime")
    @ResponseBody
    public ActResult extendTime(String courseId , String code , Integer expireTime) {
        if (expireTime == null || expireTime <= 0) {
            expireTime = 1;
        }

        ActResult result = new ActResult();
        String msg = "";
        if (!StringUtils.isEmpty(courseId) && redisUtil.exists(RedisKey.ll_live_channel_rel + courseId)) {
            //建立关系,1天后自然过期
            redisUtil.expire(RedisKey.ll_live_channel_rel + courseId ,60 * 60 * 24 * expireTime );
            msg += "设置key:" + (RedisKey.ll_live_channel_rel + courseId) + "延长成功,延长:" + expireTime + "天";
        } else {
            msg += "不存在key:" + (RedisKey.ll_live_channel_rel + courseId) + "延长失败";
        }
        String key = RedisKey.ll_ID_live_channel_rel + code;
        if (!StringUtils.isEmpty(code) && redisUtil.exists(key)) {
            //建立关系,1天后自然过期
            redisUtil.expire(key ,60 * 60 * 24 * expireTime);
            msg += "设置key:" + key + "延长成功,延长:" + expireTime + "天";
        } else {
            msg += "不存在key:" + key + "延长失败";
        }
        return result;
    }



    /**
     * 发布消息
     */
    @RequestMapping("/publish")
    @ResponseBody
    public ActResult publish(String channel,   String message) {
        ActResult result = new ActResult();
        redisUtil.publish(channel, message);
        return result;
    }

    /**
     *
     */
    @RequestMapping("/type")
    @ResponseBody
    public ActResult type(String key) {
        ActResult result = new ActResult();
        result.setData(redisUtil.type(key));
        return result;
    }


    /**
     * 发布消息
     */
    @RequestMapping("/ttl")
    @ResponseBody
    public ActResult ttl(String key) {
        ActResult result = new ActResult();
        result.setData(redisUtil.ttl(key));
        return result;
    }

    @RequestMapping("/get")
    @ResponseBody
    public ActResult get( String key) {
        ActResult result = new ActResult();
        result.setData(redisUtil.get(key));
        return result;
    }
    @RequestMapping("/exists")
    @ResponseBody
    public ActResult exists( String key) {
        ActResult result = new ActResult();
        result.setData(redisUtil.exists(key));
        return result;
    }
    @RequestMapping("/hexists")
    @ResponseBody
    public ActResult hexists( String key , String field) {
        ActResult result = new ActResult();
        result.setData(redisUtil.hexists(key, field));
        return result;
    }
    @RequestMapping("/setex")
    @ResponseBody
    public ActResult setex( String key,  int seconds,  String value) {
        ActResult result = new ActResult();
        redisUtil.setex(key, seconds, value);
        return result;
    }
    @RequestMapping("/set")
    @ResponseBody
    public ActResult set(  String key,   String value) {
        ActResult result = new ActResult();
        redisUtil.set(key, value);
        return result;
    }



    /**
     加数
     * @param key
     * @param count
     * @return
     */
    @RequestMapping("/incrBy")
    @ResponseBody
    public ActResult  incrBy(  String key,   long count) {
        ActResult result = new ActResult();
        result.setData(redisUtil.incrBy(key, count));
        return result;
    }

    /**
     自增
     * @param key
     * @return
     */
    @RequestMapping("/incr")
    @ResponseBody
    public ActResult  incr( String key) {
        ActResult result = new ActResult();
        result.setData(redisUtil.incr(key));
        return result;
    }
    /**
     自减
     * @param key
     * @return
     */
    @RequestMapping("/decr")
    @ResponseBody
    public ActResult  decr( String key) {
        ActResult result = new ActResult();
        result.setData(redisUtil.decr(key));
        return result;
    }
    /**
     减数
     * @param key
     * @param count
     * @return
     */
    @RequestMapping("/decrBy")
    @ResponseBody
    public ActResult   decrBy( String key,  long count) {
        ActResult result = new ActResult();
        result.setData(redisUtil.decrBy(key, count));
        return result;
    }
    @RequestMapping("/del")
    @ResponseBody
    public ActResult del(String... keys) {
        ActResult result = new ActResult();
        boolean flag = redisUtil.del(keys) ;
        result.setData(flag);
        return result;
    }
    @RequestMapping("/append")
    @ResponseBody
    public ActResult  append(  String key,   String value) {
        redisUtil.append(key, value);
        return ActResult.success();
    }
    @RequestMapping("/lpush")
    @ResponseBody
    public ActResult  lpush(  String key,   String... values) {
        ActResult result = new ActResult();
        result.setData(redisUtil.lpush(key, values));
        return result;
    }
    @RequestMapping("/lrange")
    @ResponseBody
    public ActResult  lrange(  String key,   int start,   int end) {
        ActResult result = new ActResult();
        result.setData(redisUtil.lrange(key, start, end));
        return result;
    }
    @RequestMapping("/llen")
    @ResponseBody
    public ActResult llen( String key) {
        ActResult result = new ActResult();
        result.setData(redisUtil.llen(key));
        return result;
    }
    @RequestMapping("/hmset")
    @ResponseBody
    public ActResult hmset( String key,  Map<String , String> map) {
        ActResult result = new ActResult();
        redisUtil.hmset(key, map);
        return result;
    }
    @RequestMapping("/hmsetSec")
    @ResponseBody
    public ActResult hmsetSec( String key,  Map<String , String> map , int seconds) {
        ActResult result = new ActResult();
        redisUtil.hmset(key, map);
        redisUtil.expire(key, seconds);
        return result;
    }

    /**
     * 创建列表 --- 有效时间
     * @param key
     * @param list
     * @param seconds
     */
    @RequestMapping("/lpushlistSec")
    @ResponseBody
    public ActResult lpushlist( String key, List<String> list , int seconds){
        if (list == null || list.size() == 0) {
            return ActResult.fail("list 大小为 0");
        }
        redisUtil.lpush(key, list.toArray(new String[0]));
        redisUtil.expire(key, seconds);
        return ActResult.success();
    }

    /**
     * 创建列表
     * @param key
     * @param list
     */
    @RequestMapping("/lpushlist")
    @ResponseBody
    public ActResult lpushlist( String key, List<String> list ){
        if (list == null || list.size() == 0) {
            return ActResult.success();
        }
        redisUtil.lpush(key, list.toArray(new String[0]));
        return ActResult.success();
    }

    /**
     * 往右边插入 创建列表
     * @param key
     * @param list
     */
    @RequestMapping("/rpushlist")
    @ResponseBody
    public ActResult rpushlist( String key, List<String> list ){
        if (list == null || list.size() == 0) {
            return ActResult.success();
        }
        redisUtil.rpushlist(key,list);
        return ActResult.success();
    }
    /**
     * 获取所有列表记录
     * @param key
     * @return
     */
    @RequestMapping("/lrangeall")
    @ResponseBody
    public ActResult  lrangeall( String key) {
        List<String> list = redisUtil.lrange(key, 0, -1);
        return ActResult.success(list);
    }
    @RequestMapping("/hset")
    @ResponseBody
    public ActResult hset(  String key,   String hkey,   String value) {
        redisUtil.hset(key, hkey, value);
        return ActResult.success();
    }
    @RequestMapping("/hdel")
    @ResponseBody
    public ActResult hdel( String key,  String... hkeys) {
        return ActResult.success(redisUtil.hdel(key, hkeys));
    }
    @RequestMapping("/hincrBy")
    @ResponseBody
    public ActResult hincrBy( String key,  String hkey,  long value) {
        return ActResult.success(redisUtil.hinc(key, hkey, value));
    }

    @RequestMapping("/hlen")
    @ResponseBody
    public ActResult hlen( String key) {
        return ActResult.success( redisUtil.hlen(key));
    }
    @RequestMapping("/hkeys")
    @ResponseBody
    public ActResult  hkeys( String key) {
        return ActResult.success(redisUtil.hkeys(key));
    }
    @RequestMapping("/hvals")
    @ResponseBody
    public ActResult  hvals( String key) {
        return ActResult.success(redisUtil.hvals(key));
    }
    @RequestMapping("/hmget")
    @ResponseBody
    public ActResult  hmget( String key,  String... hkey) {
        return ActResult.success(redisUtil.hmget(key, hkey));
    }
    @RequestMapping("/hmgetAll")
    @ResponseBody
    public ActResult  hmgetAll( String key) {
        return ActResult.success(redisUtil.hmgetAll(key));
    }
    @RequestMapping("/hget")
    @ResponseBody
    public ActResult  hget( String key,  String hkey) {
        return ActResult.success(redisUtil.hget(key, hkey));
    }
    @RequestMapping("/expire")
    @ResponseBody
    public ActResult  expire( String key , int seconds) {
        redisUtil.expire(key , seconds);
        return ActResult.success();
    }
    @RequestMapping("/lpop")
    @ResponseBody
    public ActResult  lpop( String key) {
        return ActResult.success(redisUtil.lpop(key));
    }
    @RequestMapping("/rpop")
    @ResponseBody
    public ActResult  rpop( String key) {
        return ActResult.success(redisUtil.rpop(key));
    }
    /**
     * set添加
     * @param key
     * @param member
     * @return
     */
    @RequestMapping("/sadd")
    @ResponseBody
    public ActResult sadd( String key, String member) {
        return ActResult.success(redisUtil.sadd(key, member));
    }
    /**
     * set移出
     * @param key
     * @param member
     * @return
     */
    @RequestMapping("/srem")
    @ResponseBody
    public ActResult srem( String key, String member) {
        return ActResult.success(redisUtil.srem(key, member));
    }
    /**
     * set数量
     * @param key
     * @return
     */
    @RequestMapping("/scard")
    @ResponseBody
    public ActResult scard(String key) {
        return ActResult.success(redisUtil.scard(key));
    }
    /**
     * 取得set列表
     * @param key
     * @return
     */
    @RequestMapping("/smembers")
    @ResponseBody
    public ActResult smembers( String key) {
        return ActResult.success(redisUtil.smembers(key));
    }
    /**
     * redisCluster实现keys方法
     * @param pattern
     * @return
     */
    @RequestMapping("/keys")
    @ResponseBody
    public ActResult  keys(String pattern){
        return ActResult.success( redisUtil.keys(pattern));
    }
    @RequestMapping("/zadd")
    @ResponseBody
    public ActResult  zadd( String key  , String member  ,double score) {
        return ActResult.success(redisUtil.zadd(key, member, score));
    }
    @RequestMapping("/zrem")
    @ResponseBody
    public ActResult zrem( String key  , String member) {
        return ActResult.success(redisUtil.zrem(key, member));
    }
    @RequestMapping("/zcard")
    @ResponseBody
    public ActResult zcard( String key) {
        return ActResult.success(redisUtil.zcard(key));
    }
    @RequestMapping("/zscore")
    @ResponseBody
    public ActResult  zscore( String key , String member){
        return ActResult.success(redisUtil.zscore(key, member));
    }
    @RequestMapping("/sismember")
    @ResponseBody
    public ActResult  sismember( String key , String member) {
        return ActResult.success( redisUtil.sismember(key, member));
    }

    /**
     * 取得zset列表
     * @param key
     * @return
     */
    @RequestMapping("/zrange")
    @ResponseBody
    public ActResult zrange( String key, long start , long end) {
        return ActResult.success(redisUtil.zrange(key , start , end));
    }

    @RequestMapping("/lrem")
    @ResponseBody
    public ActResult  lrem( String key  , String member) {
        return ActResult.success(redisUtil.lrem(key, member));
    }

}
