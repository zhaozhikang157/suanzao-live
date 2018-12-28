package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.dto.ShareRecordDto;
import com.longlian.model.ShareRecord;

/**
 * Created by admin on 2017/2/24.
 */
public interface ShareRecordService {

    ActResultDto insertShare(ShareRecordDto share);
}
