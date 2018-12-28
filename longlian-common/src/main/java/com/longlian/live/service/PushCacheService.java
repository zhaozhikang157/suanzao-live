package com.longlian.live.service;

import java.util.List;

/**
 * Created by admin on 2018/1/30.
 *
 * 预热接口
 */
public interface PushCacheService {

    /**
     * 预热普通地址
     * @param cacheTs
     */
    public void pushCacheUrl(String cacheTs);

    /**
     * 预热m3u8格式文件
     * @param address
     * @throws Exception
     */
    void pushCacheAddress(String address) throws Exception;

    /**
     * 预热普通地址 -- 集合
     * @param list
     */
    public void pushCachListeUrl(List<String> list);



}
