package com.longlian.live.service.impl;

import com.huaxin.util.*;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.InviCodeItemDto;
import com.longlian.live.dao.InviCodeItemMapper;
import com.longlian.live.dao.InviCodeMapper;
import com.longlian.live.service.InviCodeItemService;
import com.longlian.model.InviCode;
import com.longlian.model.InviCodeItem;
import com.longlian.token.AppUserIdentity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by admin on 2017/8/29.
 */
@Service("inviCodeItemService")
public class InviCodeItemServiceImpl implements InviCodeItemService {
    private static Logger log = LoggerFactory.getLogger(InviCodeItemServiceImpl.class);

    @Autowired
    InviCodeItemMapper inviCodeItemMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    InviCodeMapper inviCodeMapper;

    /**
     * 根据批次获取该邀请码的使用详情
     * @param page
     * @param inviCodeId
     * @return
     */
    @Override
    public List<InviCodeItemDto> getInviCodeItemPage(DataGridPage page, long inviCodeId) {
        return inviCodeItemMapper.getInviCodeItemPage(page,inviCodeId);
    }

    @Override
    public ExportExcelWhaUtil importExcelInviCodeItem(HttpServletRequest request, HttpServletResponse response, Long inviCodeId) {
        List<Map> maps = inviCodeItemMapper.getAllInviCodeItem(inviCodeId);
        for(Map map : maps){
            if(map.get("useTime")==null){
                map.put("isUse","未使用");
                map.put("useTime","");
            }else{
                map.put("isUse","已使用");
                map.put("useTime",map.get("useTime").toString().substring(0,map.get("useTime").toString().length()-2));
            }
        }
        List<String> listStr = new ArrayList<String>();
        String top1 = "邀请码列表";
        listStr.add(top1);
        String top3 = "序号,邀请码,使用状态,使用人,使用时间";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "inviCode,isUse,useName,useTime";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(maps, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "邀请码列表", request, exceltitel);
        return excel;
    }

    @Override
    public void insertItem(long inviCodeId, int num, long courseId) {
        List<String> list = new ArrayList<String>();
        for(int i = 0 ; i < num ; i++){
            String code = Utility.getIntRandom(16);
            Boolean bo = redisUtil.sismember( RedisKey.ll_invi_code + courseId, code);
            if(!bo){
                redisUtil.sadd(RedisKey.ll_invi_code + courseId,code);
                list.add(code);
            }else{
                i--;
            }

        }
        inviCodeItemMapper.insertCode(inviCodeId, list);
        long length = redisUtil.llen(RedisKey.ll_get_no_invi_code_course + inviCodeId);
        if(length < 1){
            List<Long> itemList = inviCodeItemMapper.getNoUseInviCode(inviCodeId);
            for(Long itemId : itemList){
                redisUtil.lpush(RedisKey.ll_get_no_invi_code_course + inviCodeId , itemId + "");
            }
        }
       // redisUtil.expire(RedisKey.ll_invi_code + courseId,10*60);
    }

    @Override
    public List<Long> getNoUseInviCode(long inviCodeId) {
        return inviCodeItemMapper.getNoUseInviCode(inviCodeId);
    }

    @Override
    public InviCodeItem getItemInfo(long itemId) {
        return inviCodeItemMapper.getItemInfo(itemId);
    }

    @Override
    public void updateUseAppId(long useId, long id ,Date date ,  long inviCodeId) {
        inviCodeItemMapper.updateUseAppId(useId, id ,date);
        inviCodeMapper.updateBalanceCount(inviCodeId);
    }

    @Override
    public void updateUseAppIdTwo(long useId, long id, Date time, long inviCodeId) {
        inviCodeItemMapper.updateUseAppId(useId, id ,time);
    }

    @Override
    public InviCodeItem getItemInfoByInviCode(String inviCode, Long inviCodeId) {
        return inviCodeItemMapper.getItemInfoByInviCode(inviCode,inviCodeId);
    }

    @Override
    public void updateUseTime(InviCodeItem item) {
        inviCodeItemMapper.updateUseTime(item.getId());
        inviCodeMapper.updateBalanceCount(item.getInviCodeId());
    }

    public List<ExcelTop> getExceltitel(List<String> strs) {
        List<ExcelTop> ets = new ArrayList<ExcelTop>();//行集合
        if (strs != null && strs.size() > 0) {
            for (int i = 0; i < strs.size(); i++) {
                List<String> titleList = ExportExcelWhaUtil.getTitleList(strs.get(i));
                ExcelTop et = new ExcelTop();//每行第一个位置
                et.setRowIndex(i);
                et.setRowText(0);
                et.setSl(false);
                et.setData(titleList.get(0));
                List<ExcelTop> etscell = new ArrayList<ExcelTop>();//每行内的 每列集合
                if (titleList != null && titleList.size() > 1) {
                    for (int j = 1; j < titleList.size(); j++) {
                        ExcelTop etj = new ExcelTop();
                        etj.setRowIndex(i);
                        etj.setRowText(j);
                        etj.setSl(false);
                        etj.setData(titleList.get(j));
                        etscell.add(etj);
                    }
                }
                et.setEts(etscell);
                ets.add(et);
            }
        }
        return ets;
    }

    /**
     * 获取邀请码是否被使用
     * @param inviCode
     * @param courseId
     * @return
     */
    @Override
    public Map getItemInfoByInviCodeAndcourseId(String inviCode, Long courseId) {
        return inviCodeItemMapper.getItemInfoByInviCodeAndCourseId(inviCode , courseId );
    }

    @Override
    public String findCourseName(long itemId) {
        return inviCodeItemMapper.findCourseName(itemId);
    }
}
