package com.longlian.live.service.impl;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.PageUrlMapper;
import com.longlian.live.service.PageUrlService;
import com.longlian.model.PageUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-07-04.
 */
@Service("pageUrlService")
public class PageUrlServiceImpl implements PageUrlService {
    @Autowired
    public PageUrlMapper pageUrlMapper;
    @Autowired
    public RedisUtil redisUtil;

    @Override
    public void savePageUrl(String url) {
        PageUrl pageUrl = new PageUrl();
        pageUrl.setPageUrl(url);

        //不存在，则加载
        if (!redisUtil.exists(RedisKey.ll_page_url)) {
            List<PageUrl> list = pageUrlMapper.selectAll();
            for (PageUrl u : list) {
                redisUtil.hset(RedisKey.ll_page_url , url , String.valueOf(u.getId()));
            }
        }
        if (redisUtil.hexists(RedisKey.ll_page_url , url)) {
            return ;
        }
        PageUrl temp = pageUrlMapper.selectByUrl(pageUrl.getPageUrl());
        //没找到
        if (temp == null) {
            pageUrlMapper.insert(pageUrl);
        } else {
            pageUrl = temp;
        }
        redisUtil.hset(RedisKey.ll_page_url , url , String.valueOf(pageUrl.getId()));
        return ;
    }

  public  List<Map> getPageUrlListPage(DatagridRequestModel datagridRequestModel,Map map){

      return pageUrlMapper.getPageUrlListPage(datagridRequestModel,map);
  }
    public void insertPageUrl(PageUrl pageUrl){
        if(null == pageUrl.getId()){    //保存
            pageUrlMapper.insert(pageUrl);
        }else{      //修改
            pageUrlMapper.updateByPrimaryKeySelective(pageUrl);
        }
    }
    public  PageUrl findById(long id){
        return pageUrlMapper.selectByPrimaryKey(id);
    }
    public void deleteById(long id) throws Exception{
        pageUrlMapper.deleteByPrimaryKey(id);
    }
}
