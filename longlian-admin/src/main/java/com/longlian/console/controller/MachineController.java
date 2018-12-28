package com.longlian.console.controller;

/**
 * Created by liuhan on 2017-06-11.
 */

import com.github.pagehelper.StringUtil;
import com.huaxin.util.ActResult;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.service.MobileVersionService;
import com.longlian.dto.UserMachineInfoDto;
import com.longlian.live.service.UserMachineInfoService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.jpush.JPushLonglian;
import com.longlian.model.UserMachineInfo;
import com.longlian.token.ConsoleUserIdentity;
import com.longlian.type.LogType;
import com.longlian.type.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequestMapping("/machine")
@Controller
public class MachineController {
    private static Logger log = LoggerFactory.getLogger(MachineController.class);

    @Autowired
    UserMachineInfoService userMachineInfoService;

    @Autowired
    MobileVersionService mobileVersionService;


    /**
     * 机器管理
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("/func/machine/index");
    }


    /**
     * 展示数据
     **/
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public DatagridResponseModel getList(DatagridRequestModel datagridRequestModel, UserMachineInfoDto userMachineInfo) {
        DatagridResponseModel model = new DatagridResponseModel();
        model.setRows(userMachineInfoService.getListPage(datagridRequestModel, userMachineInfo));
        model.setTotal(datagridRequestModel.getTotal());
        return model;
    }

    /**
     * 发送数据
     **/
    @RequestMapping(value = "/sendUpdateMsg", method = RequestMethod.POST)
    @ResponseBody
    public ActResult sendUpdateMsg(HttpServletRequest request, Long id) {
        ActResult result = new ActResult();
        String content = "版本升级提醒";
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            Map map1 = new HashMap();
            map1.put("NotificationType",  MsgType.SYS_VERSION_UPDATE.getTypeStr());
            UserMachineInfo umi = userMachineInfoService.getUserMachineInfo(id);

            if (mobileVersionService.isCanSend(umi))  {
                JPushLonglian.sendToUsers(new String[]{umi.getMachineCode()} ,content , map1 );
                SystemLogUtil.saveSystemLog(LogType.app_version_update_remind.getType()
                        , "1"
                        ,   token.getId()
                        ,   token.getName()
                        , umi.getMachineCode()
                        ,   "客户端：" + umi.getMachineCode() +",登录人："+umi.getUserId()+"已发送系统更新提醒");
            } else {
                result.setSuccess(false);
                result.setMsg("不需要更新");
            }
        } catch (Exception e) {
            result.setSuccess(false);
            log.error("处理失败", e);
        }
        return result;
    }

    /**
     * 发送选中数据
     **/
    @RequestMapping(value = "/sendSelect", method = RequestMethod.POST)
    @ResponseBody
    public ActResult sendSelect(HttpServletRequest request, String ids) {
        ActResult result = new ActResult();
        String content = "版本升级提醒";
        try {
            ConsoleUserIdentity token = (ConsoleUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            Map map1 = new HashMap();
            map1.put("NotificationType",  MsgType.SYS_VERSION_UPDATE.getTypeStr());

            Set<String> codes = new HashSet();
            List<UserMachineInfo> connotRemind = new ArrayList<>();
            List<UserMachineInfo> needRemind = new ArrayList<>();
            if (!StringUtil.isEmpty(ids)) {
                StringBuffer notNeedRemindSb = new StringBuffer();
                StringBuffer needRemindSb = new StringBuffer();
                String[] idArray = ids.split(",");
                for (String id : idArray) {
                    UserMachineInfo umi = userMachineInfoService.getUserMachineInfo(Long.parseLong(id));
                    if (mobileVersionService.isCanSend(umi))  {
                        codes.add(umi.getMachineCode());
                        needRemind.add(umi);
                    } else {
                        connotRemind.add(umi);
                    }
                }
                if (codes.size() > 0) {
                    JPushLonglian.sendToUsers(codes.toArray(new String[0]) ,content , map1 );
                    if (needRemind.size() > 0) {
                        for (UserMachineInfo us : needRemind) {
                            needRemindSb.append(us.getMachineCode()).append(",");
                        }
                    }

                }
                if (connotRemind.size() > 0) {
                    for (UserMachineInfo us : connotRemind) {
                        notNeedRemindSb.append(us.getMachineCode()).append(",");
                    }
                }
                String str = "";
                if (needRemindSb.length() > 0 ) {
                    str = "客户端：" +needRemindSb.toString()+"已发送系统更新提醒";
                    if (notNeedRemindSb.length() > 0 ) {
                        str+= ",";
                    }
                }

                if (notNeedRemindSb.length() > 0 ) {
                    str += "客户端：" + notNeedRemindSb.toString() + "未发送系统更新提醒";
                }
                SystemLogUtil.saveSystemLog(LogType.app_version_update_remind.getType()
                        , "1"
                        ,   token.getId()
                        ,   token.getName()
                        , ""
                        ,   str);
                result.setMsg("未发送升级信息：" + notNeedRemindSb.toString() + ",其它发送成功");
            }

        } catch (Exception e) {
            result.setSuccess(false);
            log.error("处理失败", e);
        }
        return result;
    }


}
