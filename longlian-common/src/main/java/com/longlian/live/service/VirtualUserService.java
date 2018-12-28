package com.longlian.live.service;

import com.longlian.model.Course;

import java.util.Map;

/**
 * Created by liuhan on 2017-07-13.
 */
public interface VirtualUserService {

    public Map addVirtualUser(Course course , String[] accids) throws Exception ;

    public String[] getString(Object[] ss);
}
