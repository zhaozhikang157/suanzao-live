package com.huaxin.util.redis;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huaxin.util.spring.SpringContextUtil;

import redis.clients.jedis.*;


/**
 * Created by lh on 2016/3/28.
 */
@Component("redisUtil")
public class RedisUtil implements InitializingBean {

    private static Logger log = LoggerFactory.getLogger(RedisUtil.class);

    @Value("${redis.node}")
    private String nodes;
    @Value("${redis.timeout:5000}")
    private Integer timeout;
    @Value("${redis.maxRedirections:50}")
    private Integer maxRedirections;
    @Value("${redis.maxTotal:100}")
    private Integer maxTotal;
    @Value("${redis.maxIdle:20}")
    private Integer maxIdle;

    private static RedisUtil redisUtil;
	public static RedisUtil getRedisUtil() {
		if (redisUtil == null) {
		    synchronized (RedisUtil.class) {
                if (redisUtil == null) {
                    redisUtil =  (RedisUtil)SpringContextUtil.getBeanByName("redisUtil");
                }
            }
		}
		return redisUtil;
	}


    private JedisCluster jedisCluster;

    private static Set<HostAndPort> loadHP(String nodes) {
        Set<HostAndPort> haps = new HashSet<HostAndPort>();
        log.info(nodes);
        String[] host = nodes.split(",");

        for (String ipStr : host) {
            String[] str = ipStr.split("\\:");
            String ipTemp = str[0];
            String[] ports = str[1].split("-");
            if (ports.length == 0) {
                continue;
            }
            if (ports.length > 1) {
                int min = Integer.parseInt(ports[0]);
                int max = Integer.parseInt(ports[1]);

                if (max <= min) {
                    haps.add(new HostAndPort(ipTemp , Integer.parseInt(ports[0])));
                    haps.add(new HostAndPort(ipTemp , Integer.parseInt(ports[1])));
                } else {
                    for (int i = min ;i <= max ;i++) {
                        haps.add(new HostAndPort(ipTemp , i));
                    }
                }
            } else {
                haps.add(new HostAndPort(ipTemp , Integer.parseInt(ports[0])));
            }

        }
        return haps;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);

        Set<HostAndPort> haps = loadHP(nodes);

        jedisCluster = new JedisCluster(haps , timeout , maxRedirections , config);
    }

    /**
     * 发布消息
     */
    public void publish(String channel,   String message) {
        jedisCluster.publish(channel, message);
    }

    /**
     * 订阅表达式频道
     * @param jedisPubSub
     * @param patterns
     */
    public void psubscribe(JedisPubSub jedisPubSub,  String... patterns) {
        jedisCluster.psubscribe(jedisPubSub, patterns);
    }

    /**
     * 订阅频道
     * @param jedisPubSub
     * @param patterns
     */
    public void subscribe(JedisPubSub jedisPubSub,  String... patterns) {
        jedisCluster.subscribe(jedisPubSub, patterns);
    }

    public String get(final String key) {
       return jedisCluster.get(key);
    }

    public boolean exists(final String key) {
        return jedisCluster.exists(key);
    }
    public boolean hexists(final String key ,final String field) {
        return jedisCluster.hexists(key, field);
    }
    public void setex(final String key, final int seconds, final String value) {
        jedisCluster.setex(key, seconds, value);
    }

    public void set(final String key, final String value) {
        jedisCluster.set(key, value);
    }



    /**
     加数
     * @param key
     * @param count
     * @return
     */
    public long incrBy(final String key, final long count) {
        return jedisCluster.incrBy(key, count);
    }

    /**
        自增
     * @param key
     * @return
     */
    public long incr(final String key) {
        return jedisCluster.incr(key);
    }
    /**
     自减
     * @param key
     * @return
     */
    public long decr(final String key) {
        return jedisCluster.decr(key);
    }
    /**
            减数
     * @param key
     * @param count
     * @return
     */
    public long decrBy(final String key, final long count) {
        return jedisCluster.decrBy(key, count);
    }

    public boolean del(final String... keys) {
        return jedisCluster.del(keys) == keys.length ? true : false;
    }

    public void append(final String key, final String value) {
        jedisCluster.append(key, value);
    }

    public Long lpush(final String key, final String... values) {
        return jedisCluster.lpush(key, values);
    }

    public List lrange(final String key, final int start, final int end) {
        return jedisCluster.lrange(key, start, end);
    }

    public long llen(final String key) {
        return jedisCluster.llen(key);
    }

    public void hmset(final String key, final Map<String , String> map) {
        jedisCluster.hmset(key, map);
    }
    public void hmset(final String key, final Map<String , String> map , int seconds) {
        jedisCluster.hmset(key, map);
        jedisCluster.expire(key, seconds);
    }

    /**
     * 创建列表 --- 有效时间
     * @param key
     * @param list
     * @param seconds
     */
    public void lpushlist(final String key, List<String> list , int seconds){
        if (list == null || list.size() == 0) {
            return ;
        }
        jedisCluster.lpush(key, list.toArray(new String[0]));
        jedisCluster.expire(key, seconds);
    }

    /**
     * 创建列表
     * @param key
     * @param list
     */
    public void lpushlist(final String key, List<String> list ){
        if (list == null || list.size() == 0) {
            return ;
        }
        jedisCluster.lpush(key, list.toArray(new String[0]));
    }

    /**
     * 往右边插入 创建列表
     * @param key
     * @param list
     */
    public void rpushlist(final String key, List<String> list ){
        if (list == null || list.size() == 0) {
            return ;
        }
        jedisCluster.rpush(key, list.toArray(new String[0]));
    }
    /**
     * 获取所有列表记录
     * @param key
     * @return
     */
    public List lrangeall(final String key) {
        return jedisCluster.lrange(key, 0, -1);
    }

    public void hset(final String key, final String hkey, final String value) {
        jedisCluster.hset(key, hkey, value);
    }

    public Long hsetnx(final String key, final String hkey, final String value) {
        return  jedisCluster.hsetnx(key, hkey, value);
    }

    public Long setnx(final String key, final String value) {
        return jedisCluster.setnx(key, value);
    }

    public Long hdel(final String key, final String... hkeys) {
        return jedisCluster.hdel(key, hkeys);
    }

    public Long hinc(final String key, final String hkey, final long value) {
        return jedisCluster.hincrBy(key, hkey, value);
    }


    public long hlen(final String key) {
        return jedisCluster.hlen(key);
    }

    public Set hkeys(final String key) {
         return jedisCluster.hkeys(key);
    }

    public List<String> hvals(final String key) {
        return jedisCluster.hvals(key);
   }

    public List hmget(final String key, final String... hkey) {
         return jedisCluster.hmget(key, hkey);
    }
    public  Map<String, String> hmgetAll(final String key) {
        return jedisCluster.hgetAll(key);
    }

    public String hget(final String key, final String hkey) {
        return jedisCluster.hget(key, hkey);
    }

    public void expire(final String key , int seconds) {
        jedisCluster.expire(key , seconds);
    }

	public String lpop(final String key) {
        return jedisCluster.lpop(key);
	}
    public String rpop(final String key) {
        return jedisCluster.rpop(key);
    }

	/**
	 * set添加
	 * @param key
	 * @param member
	 * @return
	 */
	public long sadd(final String key, String member) {
        return jedisCluster.sadd(key, member);
    }
	/**
	 * set移出
	 * @param key
	 * @param member
	 * @return
	 */
	public long srem(final String key, String member) {
        return jedisCluster.srem(key, member);
    }
	/**
	 * set数量
	 * @param key
	 * @return
	 */
	public long scard(final String key) {
        return jedisCluster.scard(key);
    }
	/**
	 * 取得set列表
	 * @param key
	 * @return
	 */
	public Set<String> smembers(final String key) {
        return jedisCluster.smembers(key);
    }
	/**
	 * redisCluster实现keys方法
	 * @param pattern
	 * @return
	 */
    public Set<String> keys(String pattern){
        Set<String> keys = new HashSet<String>();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        log.debug("Start getting keys by pattern "+ pattern +"...");
        for(String k : clusterNodes.keySet()){
            JedisPool jp = clusterNodes.get(k);
            Jedis connection = jp.getResource();
            try {
                keys.addAll(connection.keys(pattern));
            } catch(Exception e){
            } finally{
                log.debug("Connection closed.");
                connection.close();//用完一定要close这个链接！！！
            }
        }
        log.debug("Keys gotten!");
        return keys;
    }

    public long zadd(final String key  , String member  ,double score) {
        return jedisCluster.zadd(key, score, member);
    }

    public Double zincrby(final String key  ,double score, String member) {
        return jedisCluster.zincrby(key, score, member);
    }

    public long zrem(final String key  , String member) {
        return jedisCluster.zrem(key, member);
    }

    public long zcard(final String key) {
        return jedisCluster.zcard(key);
    }

    public Double zscore(final String key , String member){
        return jedisCluster.zscore(key, member);
    }

    public boolean sismember(final String key , String member) {
        return jedisCluster.sismember(key, member);
    }

    /**
     * 取得zset列表
     * @param key
     * @return
     */
    public Set<String> zrange(final String key, long start , long end) {
        return jedisCluster.zrange(key , start , end);
    }
    /**
     * 取得zset列表反序
     * @param key
     * @return
     */
    public Set<String> zrevrange(final String key, long start , long end) {
        return jedisCluster.zrevrange(key , start , end);
    }
    /**
            * 取得zset列表带scores
     * @param key
     * @return
             */
    public Set<Tuple> zrangeWithScores(final String key, long start , long end) {
        return jedisCluster.zrangeWithScores(key , start , end);
    }

    /**
     * 取得zset列表带scores 反序
     * @param key
     * @return
     */
    public Set<Tuple> zrevrangeWithScores(final String key, long start , long end) {
        return jedisCluster.zrevrangeWithScores(key , start , end);
    }
    /**
     * 移出zset列表
     * @param key
     * @return
     */
    public Long zremrangeByScore(final String key,  double start, double end) {
        return jedisCluster.zremrangeByScore(key , start , end);
    }

    public long lrem(final String key  , String member) {
        return jedisCluster.lrem(key,0, member);
    }

    public Long ttl(String key) {
        return jedisCluster.ttl(key);
    }

    public String type(String key) {
        return jedisCluster.type(key);
    }
}
