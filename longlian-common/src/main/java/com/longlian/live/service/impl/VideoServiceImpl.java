package com.longlian.live.service.impl;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.VideoMapper;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.VideoService;
import com.longlian.model.Video;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by liuhan on 2017/9/6.
 */
@Service("videoService")
public class VideoServiceImpl implements VideoService {
    @Autowired
    VideoMapper videoMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CourseBaseService courseBaseService;


    @Override
    public List<Video> findByUrl(String fileURL) {
        return videoMapper.findByUrl(fileURL);
    }

    /**
     * 修改 (在app中热门视频搜索存放在redis)
     *
     * @param record
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByPrimaryKeySelective(Video record) {
        videoMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public void save(Video video) {
        videoMapper.insert(video);
    }

    @Override
    public List<Video> getAllNoConvertVideo() {
        return videoMapper.getAllNoConvertVideo();
    }

    @Override
    public void saveConvertSuccessStauts(Video v) {
        if (v.getConvertAddress() != null && v.getConvertAddress().startsWith("http://")) {
            v.setConvertAddress(v.getConvertAddress().replace("http://", "https://"));
        }

        this.updateByPrimaryKeySelective(v);
        //转化成功,修改课程address
        if (v.getConvertStatus() == 1) {
            String address = v.getConvertAddress();
            if (address != null) {
                address = address.replace("https://longlian-output.oss-cn-beijing.aliyuncs.com" , "http://file3.llkeji.com");
                if(StringUtils.isNotEmpty(address)){
                    redisUtil.lpush(RedisKey.push_object_cache_course_address , address);
                }
            }
            courseBaseService.updeteAddress(v.getCourseId() , address);
        }
    }

    @Override
    public void updateVideoAddress(long id, String videoAddress) {
        videoMapper.updateVideoAddress(id, videoAddress);
    }


}
