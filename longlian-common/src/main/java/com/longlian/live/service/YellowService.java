package com.longlian.live.service;

import com.longlian.model.Course;

/**
 *  鉴黄服务
 * Created by liuhan on 2017-03-06.
 */
public interface YellowService {
    /**
     * 鉴黄服务
     */
    public boolean yellow(String url , Course course) throws  Exception;


}
