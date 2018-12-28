package com.longlian.live.service;

import com.longlian.model.Video;

import java.util.List;

/**
 * Created by liuhan on 2017/9/6.
 */
public interface VideoService {
    List<Video> findByUrl(String s);

    void updateByPrimaryKeySelective(Video v);

    void save(Video video);

    List<Video> getAllNoConvertVideo();

    void saveConvertSuccessStauts(Video v);

    void updateVideoAddress(long id, String videoAddress);
}
