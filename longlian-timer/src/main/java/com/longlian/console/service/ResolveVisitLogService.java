package com.longlian.console.service;

import com.longlian.model.CdnVisit;

/**
 * Created by liuhan on 2017-11-07.
 */
public interface ResolveVisitLogService {
    public CdnVisit getLiveCdvisit(String s);

    public String getUrl(String date, String index, String tail);
}
