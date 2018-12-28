package com.longlian.console.service;

import com.longlian.model.CdnVisit;

/**
 * Created by liuhan on 2017-11-10.
 */
public interface CdnVisitService {
    public void saveCdn(CdnVisit visit, String roomKey, String courseKey, String date);

    void saveCdnVisit(CdnVisit visit);
}
