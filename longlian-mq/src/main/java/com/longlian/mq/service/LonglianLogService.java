package com.longlian.mq.service;

import com.longlian.model.CdnVisit;

/**
 * Created by admin on 2017/10/23.
 */
public interface LonglianLogService {
    void resolveAndSave(ResolveVisitLogService resolveDetailService);

}
