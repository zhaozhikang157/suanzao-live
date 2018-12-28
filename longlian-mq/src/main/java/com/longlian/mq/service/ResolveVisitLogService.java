package com.longlian.mq.service;

import com.longlian.model.CdnVisit;

import java.io.BufferedReader;

/**
 * Created by liuhan on 2017-11-07.
 */
public interface ResolveVisitLogService {
    public CdnVisit getLiveCdvisit(String s);

    public String getUrl(String date , String index , String tail);
}
