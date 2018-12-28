package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.PageUrl;
import com.longlian.model.ShareChannel;

import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-07-04.
 */
public interface PageUrlService {
    public void savePageUrl(String url);

    List<Map> getPageUrlListPage(DatagridRequestModel datagridRequestModel,Map map);
  
    void insertPageUrl(PageUrl pageUrl);
    PageUrl findById(long id);
    void deleteById(long id) throws Exception;
}
