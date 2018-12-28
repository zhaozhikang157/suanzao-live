package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.AccountTrackService;
import com.longlian.live.service.ProxyTeacherService;
import com.longlian.live.service.RewardRecordService;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.model.Account;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.fileupload.util.LimitedInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/8/18.WHA 收益记录
 */
@Controller
@RequestMapping("/accountTrack")
public class AccountTrackController {
    @Autowired
    private AccountTrackService accountTrackService;
    @Autowired
    private RewardRecordService rewardRecordService;
    @Autowired
    AccountService accountService;
    @Autowired
    ProxyTeacherService proxyTeacherService;
    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;

    /**
     * 我的钱包收支明细
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getWalletsPage.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "钱包收支明细", httpMethod = "POST", notes = "钱包收支明细")
    public ActResultDto getWalletsPage(HttpServletRequest request,Integer returnMoneyLevel,Integer pageSize , Integer offset) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> list = accountTrackService.getWalletsPageNew(token.getId(),returnMoneyLevel,pageSize,offset);
        if(list.size()>0){
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            result.setData(list);
        }else{
            result.setCode(ReturnMessageType.NO_DATA.getCode());
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return result;
    }

    /**
     * 分销达人排行榜
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/getDistributionMasterList.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "分销达人排行榜", httpMethod = "POST", notes = "分销达人排行榜")
    public ActResultDto getDistributionMasterList(@ApiParam(required =true, name = "课程ID", value = "课程ID")Long courseId,
                                                  Integer pageNum, Integer pageSize) {
        ActResultDto result = new ActResultDto();
        if (courseId==null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            List<Map> list = accountTrackService.getDistributionMasterList(courseId, pageNum, pageSize);
            if(list!=null && list.size()>0){
                result.setData(list);
                result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            }else{
                result.setCode(ReturnMessageType.NO_DATA.getCode());
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            }
        }
        return result;
    }

    /**
     * 我的收益(直播,分销,平台,代理)
     * @param request
     * @return
     */
    @RequestMapping(value = "/findAllWallet.user")
    @ResponseBody
    @ApiOperation(value = "我的收益", httpMethod = "GET", notes = "我的收益")
    public ActResultDto findAllWallet(HttpServletRequest request){
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Map map = accountTrackService.findAllWallet(token.getId());
        Map isProxy = proxyTeacherService.getProxyAppIdByTeacherId(token.getId());
        map.put("isProxy",systemParaRedisUtil.getBankOutRemark(token.getId()));
        if(isProxy!=null){
            if(isProxy.get("proxyAppId")!=null){
                if(Long.valueOf(isProxy.get("proxyAppId").toString())>0){
                    map.put("isProxy",systemParaRedisUtil.getBankOutRemarkProxy());
                }
            }
        }
        resultDto.setData(map);
        return resultDto;
    }

    /**
     * 我的今日收益（播,分销,平台,代理）
     * @param request
     * @return
     */
    @RequestMapping(value = "/findTodayWallet.user")
    @ResponseBody
    @ApiOperation(value = "我的收益", httpMethod = "GET", notes = "我的收益")
    public ActResultDto findTodayWallet(HttpServletRequest request){
        ActResultDto ac = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Map map = accountTrackService.findTodayWallet(token.getId());
        Map isProxy = proxyTeacherService.getProxyAppIdByTeacherId(token.getId());
        map.put("isProxy", systemParaRedisUtil.getBankOutRemark(token.getId()));
        if(isProxy != null){
            if(isProxy.get("proxyAppId")!=null){
                if(Long.valueOf(isProxy.get("proxyAppId").toString())>0){
                    map.put("isProxy",systemParaRedisUtil.getBankOutRemarkProxy());
                }
            }
        }
        ac.setData(map);
        return ac;
    }

    @RequestMapping(value = "/findTodayWalletNew.user")
    @ResponseBody
    @ApiOperation(value = "我的收益", httpMethod = "GET", notes = "我的收益")
    public ActResultDto findTodayWalletNew(HttpServletRequest request){
        ActResultDto ac = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Map map = accountTrackService.findTodayWalletNew(token.getId());
        Map isProxy = proxyTeacherService.getProxyAppIdByTeacherId(token.getId());
        map.put("isProxy", systemParaRedisUtil.getBankOutRemark(token.getId()));
        if(isProxy != null){
            if(isProxy.get("proxyAppId")!=null){
                if(Long.valueOf(isProxy.get("proxyAppId").toString())>0){
                    map.put("isProxy",systemParaRedisUtil.getBankOutRemarkProxy());
                }
            }
        }
        ac.setData(map);
        return ac;
    }

    /**
     * 我的今日收益 按分类（课程、分销、打赏）
     * @param request
     * @param type
     * @return
     */
    @RequestMapping(value = "/findTodayWalletByType.user")
    @ResponseBody
    @ApiOperation(value = "我的收益", httpMethod = "GET", notes = "我的收益")
    public ActResultDto findTodayWalletByType(HttpServletRequest request,
                                              @ApiParam(required =true, name = "收益类型", value = "收益类型") Integer type,
                                              @ApiParam(required =true, name = "当前页", value = "收益类型") Integer pageNum,
                                              @ApiParam(required =true, name = "每页条数", value = "收益类型") Integer pageSize){
        ActResultDto dto = new ActResultDto();
        if(type == null || type < 0){
            dto.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
            dto.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            return dto;
        }
        if(pageNum == null || pageNum <= 0){
            dto.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
            dto.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            return dto;
        }
        if(pageSize == null || pageSize <= 0){
            dto.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
            dto.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            return dto;
        }
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List list = accountTrackService.findTodayWalletByType(type,pageNum,pageSize);
        if(list != null && list.size() > 0){
            dto.setData(list);
        }
        return dto;
    }

    /**
     * 分销,单节课收益
     **/
    @RequestMapping(value = "/platIncomeDetailsPage.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "分销收益", httpMethod = "GET", notes = "分销收益")
    public ActResultDto platIncomeDetailsPage(Integer offset,Integer pageSize,HttpServletRequest request) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(pageSize == null) pageSize = 10;
        if(offset == null) offset = 0;
        List<Map> list = rewardRecordService.getplatIncomePage(offset, pageSize, token.getId());
        if(list!=null&&list.size()>0){
            resultDto.setData(list);
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        }else{
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return resultDto;
    }

    /**
     * 分销,单节课收益
     **/
    @RequestMapping(value = "/proxyIncomeDetailsPage.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "分销收益", httpMethod = "GET", notes = "分销收益")
    public ActResultDto proxyIncomeDetailsPage(Integer offset,Integer pageSize,HttpServletRequest request) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(pageSize == null) pageSize = 10;
        if(offset == null) offset = 0;

        List<Map> list = accountTrackService.getproxyIncomePage(offset, pageSize, token.getId());
        if(list!=null&&list.size()>0){
            resultDto.setData(list);
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        }else{
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return resultDto;
    }

    /**
     * 获取账户 0:没有代理 1:代理
     * @param request
     * @return
     */
    @RequestMapping("/getAccount.user")
    @ResponseBody
    @ApiOperation(value = "获取账户信息", httpMethod = "GET", notes = "获取账户信息")
    public ActResultDto getAccount(HttpServletRequest request){
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Account account = accountService.getAccountByAppId(token.getId());
        Map map = proxyTeacherService.getProxyAppIdByTeacherId(token.getId());
        resultDto.setData("0");
        if(map!=null){
            if(map.get("proxyAppId")!=null){
                if(Long.valueOf(map.get("proxyAppId").toString())>0){
                    resultDto.setData("1");
                }
            }
        }
        if(account!=null){
            if(StringUtils.isEmpty(account.getTradePwd())){
                resultDto.setCode(ReturnMessageType.NO_TRAD_PASSWORD.getCode());
                resultDto.setMessage(ReturnMessageType.NO_TRAD_PASSWORD.getMessage());
                return resultDto;
            }else{
                resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                return resultDto;
            }
        }else{
            resultDto.setCode(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getCode());
            resultDto.setMessage(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getMessage());
            return resultDto;
        }
    }

}
