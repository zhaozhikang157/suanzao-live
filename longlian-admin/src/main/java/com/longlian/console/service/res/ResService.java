package com.longlian.console.service.res;

import com.longlian.model.MRes;

import java.util.List;

public interface ResService {
    /**
     * 根据类型取得所有的资源
     * @param type
     * @return
     */
    public List<MRes> getAllResByType(String type);
    
    public List<MRes> getRes(String ids, String type);
    /**
     * 取得资源服务器的地址，方便网页访问
     * @return
     */
    public String getResServerDomain() ;
    /**
     * 取得用户通过token
     * @param userId
     * @return
     */
    public String getResAccessToken(Long userId);

    /**
     * 返回菜单修改时间
     * @return
     */
    public String getMenuUpdateTime();
}
