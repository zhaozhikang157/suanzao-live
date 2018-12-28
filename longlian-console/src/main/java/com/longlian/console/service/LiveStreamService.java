package com.longlian.console.service;

import com.huaxin.util.ActResult;

import java.util.List;
import java.util.Map;
/**
 * Created by pangchao on 2017/2/15.
 */
public interface LiveStreamService {
    List<Map>  getStreamList();
    ActResult disableStream(String courseId);

}
