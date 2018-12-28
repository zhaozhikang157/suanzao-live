package com.longlian.console.service.impl;

import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.weixin.type.WechatQRCodeType;
import com.longlian.console.dao.ShareChannelMapper;
import com.longlian.console.service.CourseService;
import com.longlian.console.service.ShareChannelService;
import com.longlian.live.dao.ChannelVisitRecordMapper;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.Course;
import com.longlian.model.ShareChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/6/16.
 */
@Component("shareChannelService")
public class ShareChannelServiceImpl implements ShareChannelService {

    @Autowired
    ShareChannelMapper shareChannelMapper;
    @Autowired
    ChannelVisitRecordMapper channelVisitRecordMapper;

    @Autowired
    WeixinUtil weixinUtil;

    @Autowired
    CourseService courseService;

    public List<Map> getShareChannelListPage(DatagridRequestModel datagridRequestModel,Map map){
        
      return  shareChannelMapper.getShareChannelListPage(datagridRequestModel,map);
    }
    @Override
    @Transactional(readOnly = true)
    public ShareChannel findById(long id) {
        return shareChannelMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveShareChannel(ShareChannel shareChannel) {
        if(null == shareChannel.getId()){    //保存
            shareChannel.setCreateTime(new Date());
            shareChannel.setStatus("0");
            shareChannelMapper.insert(shareChannel);
        }else{      //修改
            shareChannelMapper.updateByPrimaryKeySelective(shareChannel);
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String ids) {
        shareChannelMapper.deleteByIds(ids);
    }

    /**
     * 生成渠道微信公众号二维码图片
     * @param courseId
     * @param channelId
     * @return
     */
    @Override
    public String generateWeiXinQRCode(long courseId, long channelId) {
        Course course = courseService.getCourse(courseId);
        String qrCodeAddress = "";
        if(course != null ){
            qrCodeAddress =  weixinUtil.getParaQrcode(WechatQRCodeType.third_wechat_or_course_param.getValue(), course.getRoomId(), course.getId(), 0, channelId );
        }
        return qrCodeAddress;
    }

    @Override
    public  List<Map> getChannelRecordListPage(DatagridRequestModel datagridRequestModel,String name,Long courseId){

        List<Map> list =  shareChannelMapper.getChannelRecordListPage(datagridRequestModel,name);
        for(Map map : list)
        {
            Long channelId = Long.parseLong(map.get("id").toString());
            map.put("allChannelVisit",channelVisitRecordMapper.getAllChannelVisit(channelId,courseId));
            map.put("newChannelVisit",channelVisitRecordMapper.getNewChannelVisit(channelId,courseId));
        }
        return  list;
    }



}
