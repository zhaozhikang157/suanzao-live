package com.longlian.console.util;

import cn.jpush.api.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.cdn.model.v20141111.DescribeLiveSnapshotConfigRequest;
import com.aliyuncs.cdn.model.v20141111.ForbidLiveStreamRequest;
import com.aliyuncs.cdn.model.v20141111.ForbidLiveStreamResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.huaxin.util.JsonUtil;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.model.LiveStreamOnlineInfo;
import com.longlian.type.FlowManagementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by longlian007 on 2018/2/7.
 * 流管理工具类
 */
@Service("flowManagementUtil")
public class FlowManagementUtil {

    //日志输出
    private static Logger log = LoggerFactory.getLogger(FlowManagementUtil.class);
    @Value("${live.domain:}")
    private String domain;
    private static final String action = "DescribeLiveStreamsOnlineList";

    /**
     * 获取正在直播流列表
     * @return
     */
    public List<LiveStreamOnlineInfo> findLiveStreamOnlineInfos(){
        String responseJson = this.gethttpResponseJson(this.getDescribeLiveSnapshotConfigRequest(FlowManagementType.DescribeLiveStreamsOnlineList.getValue(),null,null));
        return this.getLiveStreamOnlineInfos(responseJson);
    }

    /**
     * 加入黑名单
     * @param appName
     * @param streamName
     * @return
     */
    public String liveAddBlacklist(String appName,String streamName){
        return this.ForbidLiveStreamR(appName,streamName,FlowManagementType.ForbidLiveStream.getValue()).getRequestId();
    }

    /**
     * 恢复
     * @param appName
     * @param streamName
     * @return
     */
    public String recoveryBlacklist(String appName,String streamName){
        return this.ForbidLiveStreamR(appName,streamName,FlowManagementType.ResumeLiveStream.getValue()).getRequestId();
    }



    /**
     * 获取黑名单流列表
     * @return
     */
    public List<LiveStreamOnlineInfo> findDescribeLiveStreamsBlockList(){
        String responseJson = this.gethttpResponseJson(this.getDescribeLiveSnapshotConfigRequest(FlowManagementType.DescribeLiveStreamsBlockList.getValue(),null,null));
        return this.getBlacklist(responseJson);
    }
    /**
     * 报文解析
     * @param jsons
     * @return
     */
    private List<LiveStreamOnlineInfo> getLiveStreamOnlineInfos(String jsons){
        log.info("接收到的报文:" + jsons);
        List<LiveStreamOnlineInfo> los = new ArrayList<LiveStreamOnlineInfo>();
        try{
            JSONObject object = JsonUtil.getObject(jsons);
            JSONObject onlineInfo = object.getJSONObject("OnlineInfo");
            JSONArray liveStreamOnlineInfo = onlineInfo.getJSONArray("LiveStreamOnlineInfo");
            for (java.util.Iterator tor=liveStreamOnlineInfo.iterator();tor.hasNext();) {
                LiveStreamOnlineInfo lo = new LiveStreamOnlineInfo();
                JSONObject job = (JSONObject)tor.next();
                lo.setDomainName(job.get("DomainName").toString());
                lo.setAppName(job.get("AppName").toString());
                lo.setStreamName(job.get("StreamName").toString());
                lo.setPublishTime(job.get("PublishTime").toString());
                lo.setPublishUrl(job.get("PublishUrl").toString());
                String responseJson = this.gethttpResponseJson(this.getDescribeLiveSnapshotConfigRequest(FlowManagementType.DescribeLiveStreamOnlineUserNum.getValue(),lo.getAppName(),lo.getStreamName()));
                JSONObject jo = JsonUtil.getObject(responseJson);
                lo.setUserNum(jo.getString("TotalUserNumber"));
                los.add(lo);
            }
            return los;
        }catch (Exception e) {
            log.error("报文解析失败",e);
        }
        return los;
    }

    /**
     * 报文解析
     * @param jsons
     * @return
     */
    private List<LiveStreamOnlineInfo> getBlacklist(String jsons){
        log.info("接收到的报文:" + jsons);
        List<LiveStreamOnlineInfo> los = new ArrayList<LiveStreamOnlineInfo>();
        try{
            JSONObject object = JsonUtil.getObject(jsons);
            JSONObject streamUrls = object.getJSONObject("StreamUrls");
            String domainName = object.getString("DomainName");
            JSONArray streamUrl = streamUrls.getJSONArray("StreamUrl");
            for (java.util.Iterator tor=streamUrl.iterator();tor.hasNext();) {
                LiveStreamOnlineInfo lo = new LiveStreamOnlineInfo();
                lo.setDomainName(domainName);
                String publishUrl = (String) tor.next();
                lo.setPublishUrl(publishUrl);
                if(StringUtils.isNotEmpty(publishUrl)){
                    String[] split = publishUrl.split("/");
                    if(split.length==3){
                        lo.setAppName(split[1]);
                        lo.setStreamName(split[2]);
                    }
                }
                los.add(lo);
            }
        }catch (Exception e) {
            log.error("报文解析失败",e);
        }
        return los;
    }

    private IAcsClient getIAcsClient(){
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", LonglianSsoUtil.accessKeyId, LonglianSsoUtil.accessKeySecret);
        return new DefaultAcsClient(profile);
    }

    /**
     * 获取列表
     * @return
     */
    private DescribeLiveSnapshotConfigRequest getDescribeLiveSnapshotConfigRequest(String actionName,String appName,String streamName){
        DescribeLiveSnapshotConfigRequest describeLiveSnapshotConfigRequest = new DescribeLiveSnapshotConfigRequest();
        describeLiveSnapshotConfigRequest.setActionName(actionName);
        describeLiveSnapshotConfigRequest.setDomainName(domain);
        describeLiveSnapshotConfigRequest.setProtocol(ProtocolType.HTTPS); //指定访问协议
        describeLiveSnapshotConfigRequest.setAcceptFormat(FormatType.JSON); //指定api返回格式
        describeLiveSnapshotConfigRequest.setMethod(MethodType.POST); //指定请求方法
        describeLiveSnapshotConfigRequest.setAppName(appName);
        describeLiveSnapshotConfigRequest.setStreamName(streamName);
        return describeLiveSnapshotConfigRequest;
    }

    /**
     * 禁止直播流推送
     * @return
     */
    private ForbidLiveStreamRequest getAddBlacklist(String appName,String streamName,String actionName){
        ForbidLiveStreamRequest  forbidLiveStreamRequest  = new ForbidLiveStreamRequest ();
        forbidLiveStreamRequest.setActionName(actionName);
        forbidLiveStreamRequest.setDomainName(domain);
        forbidLiveStreamRequest.setProtocol(ProtocolType.HTTPS); //指定访问协议
        forbidLiveStreamRequest.setAcceptFormat(FormatType.JSON); //指定api返回格式
        forbidLiveStreamRequest.setMethod(MethodType.POST); //指定请求方法
        forbidLiveStreamRequest.setAppName(appName);
        forbidLiveStreamRequest.setStreamName(streamName);
        forbidLiveStreamRequest.setLiveStreamType("publisher");
        return forbidLiveStreamRequest;
    }

    public String gethttpResponseJson(DescribeLiveSnapshotConfigRequest dc){
        IAcsClient iAcsClient = this.getIAcsClient();
        try {
            if(iAcsClient!=null){
                HttpResponse httpResponse = iAcsClient.doAction(dc);
                if(httpResponse!=null){
                    return new String(httpResponse.getHttpContent());
                }
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    private  ForbidLiveStreamResponse ForbidLiveStreamR(String appName,String streamName,String actionName){
        ForbidLiveStreamResponse response=null;
        IAcsClient iAcsClient = getIAcsClient();
        try {
            response = iAcsClient.getAcsResponse(this.getAddBlacklist(appName,streamName,actionName));
        }catch (ServerException e) {
            e.printStackTrace();
        }
        catch (ClientException e) {
            e.printStackTrace();
        }
        return response;
    }
}
