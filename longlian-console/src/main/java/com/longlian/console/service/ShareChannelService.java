package com.longlian.console.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.ShareChannel;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/6/16.
 */
public interface ShareChannelService {

    List<Map> getShareChannelListPage(DatagridRequestModel datagridRequestModel,Map map);
    ShareChannel findById(long id);
    void saveShareChannel(ShareChannel ShareChannel);
    void deleteById(String ids) throws Exception;

    List<Map>  getChannelRecordListPage(DatagridRequestModel datagridRequestModel,String name,Long courseId);

    String generateWeiXinQRCode(long courseId , long channelId);
}
