package com.longlian.live.service.impl;

import com.github.pagehelper.StringUtil;
import com.huaxin.util.*;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.dto.ActResultDto;
import com.longlian.live.dao.LiveChannelMapper;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.EndLiveService;
import com.longlian.live.service.LiveChannelService;
import com.longlian.live.service.RoomFuncService;
import com.longlian.live.util.LiveURLAuthUtil;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import com.longlian.model.LiveChannel;
import com.longlian.type.FuncCode;
import com.longlian.type.OssBucket;
import com.longlian.type.YunxinCustomMsgType;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.crypto.dsig.SignatureMethod;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 通道
 * Created by liuhan on 2017-02-27.
 */
@Service("liveChannelService")
public class LiveChannelServiceImpl implements LiveChannelService {
    private static Logger log = LoggerFactory.getLogger(LiveChannelServiceImpl.class);
    @Autowired
    private LiveChannelMapper liveChannelMapper;
    @Autowired
    private RedisUtil redisUtil;
    private final long exTimeIng = 300000l;     //直播中 判断掐流时间  5分钟 单位:毫秒
    @Autowired
    private RoomFuncService roomFuncService;

    @Value("${live.pushAddress:}")
    private String pushAddress;
    @Value("${live.playAddressRTMP:}")
    private String playAddressRTMP  ;
    @Value("${live.playAddressFLV:}")
    private String playAddressFLV  ;
    @Value("${live.playAddressM3U8:}")
    private String playAddressM3U8  ;
    @Value("${live.domain:}")
    private String domain;
    @Autowired
    private YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    private CourseBaseService courseBaseService;

    public void forbidLiveStream(LiveChannel liveChannel, Course course) throws Exception {
        doGet(getRequestAddress("ForbidLiveStream", liveChannel.getDomain(), String.valueOf(course.getRoomId()), String.valueOf(course.getId())));
    }

    public void resumeLiveStream(LiveChannel liveChannel, Course course) throws Exception {
        doGet(getRequestAddress("ResumeLiveStream", liveChannel.getDomain(), String.valueOf(course.getRoomId()), String.valueOf(course.getId())));
    }

    @Override
    public LiveChannel getDefaultLiveChannel() {
        LiveChannel liveChannel = new LiveChannel();
        liveChannel.setCode("live");
        liveChannel.setPushAddr( this.pushAddress);
        liveChannel.setPlayAddr1(this.playAddressRTMP);
        liveChannel.setPlayAddr2(this.playAddressFLV);
        liveChannel.setPlayAddr3(this.playAddressM3U8);
        liveChannel.setDomain(this.domain);
        return liveChannel;
    }

    /**
     * 执行一个HTTP GET请求，返回请求响应的HTML
     *
     * @param url 请求的URL地址
     * @return 返回请求响应的HTML
     */
    public static String doGet(String url) {
        HttpGet request = new HttpGet(url);
        HttpMethod method = new GetMethod(url);
        String result = "";
        try {
            HttpResponse response = HttpClients.createDefault().execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity());
            } else if (response.getStatusLine().getStatusCode() == 404) {
                log.info("当前账户下未查到域名");
                result = EntityUtils.toString(response.getEntity());
                log.info(result);
                return result;
            } else if (response.getStatusLine().getStatusCode() == 400) {
                log.info("缺少参数");
                result = EntityUtils.toString(response.getEntity());
                log.info(result);
                return result;
            } else if (response.getStatusLine().getStatusCode() == 403) {
                log.info("不支持当前操作，如：非直播类域名");
                result = EntityUtils.toString(response.getEntity());
                log.info(result);
                return result;
            } else if (response.getStatusLine().getStatusCode() == 500) {
                log.info("后台发生未知错误");
                result = EntityUtils.toString(response.getEntity());
                log.info(result);
                return result;
            }
        } catch (ClientProtocolException e) {
            log.error("发送GET请求出现异常！", e);
        } catch (IOException e) {
            log.error("发送GET请求出现异常！", e);
        }
        return result;
    }

    public static String getRequestAddress(String action, String DomainName, String appName, String streamName) throws Exception {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);


        final String HTTP_METHOD = "GET";
        Map<String, String> parameterMap = new HashMap<String, String>();

        parameterMap.put("Version", "2014-11-11");
        parameterMap.put("AccessKeyId", LonglianSsoUtil.accessKeyId);

        parameterMap.put("Timestamp", df.format(new Date()));
        parameterMap.put("SignatureMethod", "HMAC-SHA1");
        parameterMap.put("SignatureVersion", "1.0");
        parameterMap.put("SignatureNonce", UUID.randomUUID().toString());
        parameterMap.put("Format", "JSON");

        parameterMap.put("Action", action);
        parameterMap.put("DomainName", DomainName);
        parameterMap.put("AppName", appName);
        parameterMap.put("StreamName", streamName);
        parameterMap.put("LiveStreamType", "publisher");


        List<String> sortedKeys = new ArrayList<String>(parameterMap.keySet());
        Collections.sort(sortedKeys);

        final String SEPARATOR = "&";
        final String EQUAL = "=";
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(HTTP_METHOD).append(SEPARATOR);
        stringToSign.append(percentEncode("/")).append(SEPARATOR);
        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (String key : sortedKeys) {
            String value = parameterMap.get(key);
            canonicalizedQueryString.append(SEPARATOR).append(percentEncode(key)).append(EQUAL).append(percentEncode(value));

        }

        stringToSign.append(percentEncode(canonicalizedQueryString.toString().substring(1)));
        final String ALGORITHM = "HmacSHA1";
        final String secret = SsoUtil.accessKeySecret;

        SecretKey key = new SecretKeySpec((secret + SEPARATOR).getBytes(ENCODE_TYPE), SignatureMethod.HMAC_SHA1);
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(key);
        String signature = URLEncoder.encode(new String(new org.apache.commons.codec.binary.Base64().encode(mac.doFinal(stringToSign.toString().getBytes(ENCODE_TYPE))), ENCODE_TYPE), ENCODE_TYPE);

        StringBuilder requestURL;
        requestURL = new StringBuilder("https://cdn.aliyuncs.com?");
        requestURL.append(URLEncoder.encode("Signature", ENCODE_TYPE)).append("=").append(signature);
        for (Map.Entry<String, String> e : parameterMap.entrySet()) {
            requestURL.append("&").append(percentEncode(e.getKey())).append("=").append(percentEncode(e.getValue()));
        }
        return requestURL.toString();

    }
    /**
     * 取得当前课程用的直播通道
     * @param course
     * @return
     */
    public LiveChannel getCourseLiveAddr( Course course) {
        LiveChannel liveChannel = this.getDefaultLiveChannel();
        //如果是录播
        if ("1".equals(course.getIsRecorded())) {
            liveChannel.setPushAddr(course.getVideoAddress());
            liveChannel.setPlayAddr1(course.getVideoAddress());
            liveChannel.setPlayAddr2(course.getVideoAddress());
            liveChannel.setPlayAddr3(course.getVideoAddress());
        } else {
            course.setLiveAddress(course.getLiveAddress()== null ? "" : course.getLiveAddress());
            course.setHlsLiveAddress(course.getHlsLiveAddress()== null ? "" : course.getHlsLiveAddress());
            //如果是连麦
            liveChannel.setPushAddr(getPushAddress(course.getPushAddress() ,  liveChannel , String.valueOf(course.getId())));
            String convert = courseBaseService.getConvert(course.getId());
            String s =  LiveURLAuthUtil.getPlayUrl(liveChannel.getPlayAddr1().replace("/AppName/StreamName",  course.getLiveAddress()) , course.getLiveAddress());
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(convert)){
                s=s+convert;
            }

            liveChannel.setPlayAddr1(s);
            liveChannel.setPlayAddr2( LiveURLAuthUtil.getPlayUrl(liveChannel.getPlayAddr2().replace("/AppName/StreamName", course.getLiveAddress()), course.getLiveAddress() + ".flv"));
            liveChannel.setPlayAddr3( LiveURLAuthUtil.getPlayUrl(liveChannel.getPlayAddr3().replace("/AppName/StreamName", course.getHlsLiveAddress()), course.getHlsLiveAddress() + ".m3u8"));
        }
        return liveChannel;
    }

    @Override
    public String getPushAddress(String originUrl , LiveChannel liveChannel , String courseId) {
          originUrl = (originUrl == null ? "" : originUrl);
        String pushAddress = liveChannel.getPushAddr().replace("/AppName/StreamName", originUrl);
        pushAddress = LiveURLAuthUtil.getPushUrl(pushAddress , originUrl);
        return pushAddress;
    }

    @Override
    public String getPlayAddress(String roomId, String courseId, LiveChannel liveChannel) {
        String originUrl = "/" + roomId + "/" + courseId;
        String playAddress = liveChannel.getPlayAddr1().replace("/AppName/StreamName", originUrl);
        playAddress = LiveURLAuthUtil.getPlayUrl(pushAddress , originUrl);
        return playAddress;
    }

    @Override
    public void saveRecord(Course course , String domain, String env) {
        try {
            course.setVideoAddress(getVideoAddr(course , domain , env));
            courseBaseService.updateVideoAddr(course);
        } catch (Exception ex) {
            log.error(ex.toString());
            retry(course ,domain , env);
        }
    }

    @Override
    public ActResultDto liveNotify(String courseId , String action ) {
        Course course = courseBaseService.getCourseFromRedis(Long.parseLong(courseId));
        if (course == null ) {
            log.error("未找到对应的课程");
            return ActResultDto.success();
        }
        String ac = "publish".equals(action) ? "1" : "0";
        //直播流连接,则发送mq,修改course中的isConnection的状态
        if("1".equals(ac)){
            Map map = new HashMap();
            map.put("courseId",courseId);
            map.put("isConnection",1);
            redisUtil.lpush(RedisKey.course_is_connection,JsonUtil.toJson(map));
        }
        redisUtil.set(RedisKey.ll_course_live_connected  + courseId , ac);
        //是否有正在直播 -- 用于 直播中途断开,没有重新连接结束课
        redisUtil.set(RedisKey.ll_live_notify_cation + courseId, ac);
        redisUtil.expire(RedisKey.ll_live_notify_cation + courseId , 24 * 60 * 60);
        //直播流断开
        if("0".equals(ac)){
            String redisKey = RedisKey.ll_course_live_notify_cation + course.getId();
            setTimer(Long.parseLong(courseId), redisKey, "1" , null ,  exTimeIng);
        }
        //课程已经结束了就不发消息
        String isEnd = redisUtil.get(RedisKey.course_is_end + courseId);
        if ("1".equals(isEnd)) {
            return ActResultDto.success();
        }
        Map msg = new HashMap();
        //直播流断开 publish_done ,直播流愎复 publish
        msg.put("type","publish".equals(action) ? YunxinCustomMsgType.LIVE_CHANNEL_OPEN.getType() : YunxinCustomMsgType.LIVE_CHANNEL_CLOSE.getType());
        Map val = new HashMap();
        val.put("value","publish".equals(action) ? "直播流接通了" : "直播流断开了");
        msg.put("data",val);
        //给相关的聊天室人员发送一个直播流断开(恢复) 的消息
        log.info(JsonUtil.toJson(msg));
        yunxinChatRoomUtil.sendMsg(String.valueOf(course.getChatRoomId()) ,String.valueOf(course.getAppId())  , "100", JsonUtil.toJson(msg));

        Map map = new HashMap();
        map.put("action", action);
        map.put("courseId", courseId);
        redisUtil.lpush(RedisKey.ll_live_push_notify, JsonUtil.toJson(map));
        return ActResultDto.success();
    }

    /**
     * 取得课程roomToken
     *
     * @param courseId
     * @param userId
     * @return
     */
    @Override
    public  Map<String , String> getRoomToken(Long courseId, Long userId) throws Exception{
        return null;
    }

    /**
     * 删除课程room
     *
     * @param courseId
     * @return
     */
    @Override
    public void delRoom(Long courseId )   {
    }

    @Autowired
    EndLiveService endLiveService;

    private void setTimer(long courseId , String redisKey , String type , Date afterTime , long s){
        Long stamp = new Date().getTime();
        redisUtil.set(redisKey, stamp+"");
        Timer timer = new Timer();
        LiveEndServiceImpl liveTask = new LiveEndServiceImpl(courseId,stamp, endLiveService,redisUtil,type);
        if("2".equals(type)){
            timer.schedule(liveTask, afterTime);
        }else if("1".equals(type)){
            timer.schedule(liveTask,s);
        }
    }
    /**
     * 取得录制的地址
     * @param course
     * @return
     */
    public String getVideoAddr(Course course , String domain, String env) throws Exception {
        //提前半个小时索引
        long oneHour = 30 * 60 * 1000;
        String url = getRequestAddress(new Date(course.getStartTime().getTime() - oneHour)
                ,new Date()
                , domain
                , String.valueOf(course.getRoomId())
                , String.valueOf(course.getId()) , env);
        String result = doGet(url);
        Map resMap = JsonUtil.getObject(result , HashMap.class);
        String code = (String)resMap.get("Code");

        if ("RecordContent.Missing".equals(code)) {
            //如果是空的话，说明在该视频直播直播期间未-直播视频
            throw new Exception("生成视频直播失败!");
        }

        if (code != null && !"".equals(code)) {
            log.info(result);
            //如果是空的话，说明在该视频直播直播期间未-直播视频
            throw new Exception("生成视频直播失败!");
        }
        Map info = (Map)resMap.get("RecordInfo");
        String address = (String)info.get("RecordUrl");
        if (address != null) {
            address = address.replace("http://longlian-live2.oss-cn-shanghai.aliyuncs.com" , "http://file2.llkeji.com");
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(address)){
                redisUtil.lpush(RedisKey.push_object_cache_course_address , address);
            }
        }

        //如果是空的话，说明在该视频直播直播期间未-直播视频
        return  org.apache.commons.lang3.StringUtils.isEmpty(address) ? "novideo" : address ;
    }


    public void retry(Course course , String domain, String env) {
        final RetryTemplate retryTemplate = new RetryTemplate();
        final SimpleRetryPolicy policy = new SimpleRetryPolicy(3, Collections.<Class<? extends Throwable>, Boolean>
                singletonMap(Exception.class, true));
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(5000);
        retryTemplate.setRetryPolicy(policy);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        final RetryCallback<Object, Exception> retryCallback = new RetryCallback<Object, Exception>() {
            public Object doWithRetry(RetryContext context) throws Exception {

                course.setVideoAddress(getVideoAddr(course , domain , env));
                courseBaseService.updateVideoAddr(course);
                //设置context一些属性,给RecoveryCallback传递一些属性
                return null;
            }
        };
        // 如果RetryCallback执行出现指定异常, 并且超过最大重试次数依旧出现指定异常的话,就执行RecoveryCallback动作
        final RecoveryCallback<Object> recoveryCallback = new RecoveryCallback<Object>() {
            public Object recover(RetryContext context) throws Exception {
                course.setVideoAddress("novideo");
                courseBaseService.updateVideoAddr(course);
                String tip = "视频直播:" + course.getId() +"," + course.getLiveTopic() +"未取到直播地址，请注意！";
                log.error(tip);
                String receive = CustomizedPropertyConfigurer.getContextProperty("exception.receive");
                if (!Utility.isNullorEmpty(receive)) {
                    String[] res = receive.split(",");
                    EmailUtil.getInstance().send(IPUtil.getLocalIp4()+"-mq错误",tip, res);
                }
                return null;
            }
        };
        try {
            final Object execute = retryTemplate.execute(retryCallback, recoveryCallback);
        } catch (Exception e) {
            log.error("取得地址报错",e);
        }
    }


    private static final String ENCODE_TYPE = "UTF-8";

    private static String percentEncode(String value) throws Exception {
        if (value == null) return null;
        return URLEncoder.encode(value, ENCODE_TYPE).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    public static String getRequestAddress(Date startTime, Date endTime , String domain, String appName , String streamName , String env) throws Exception {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);

        String start = df.format(startTime);
        String end = df.format(endTime);


        final String HTTP_METHOD = "GET";
        Map<String, String> parameterMap = new HashMap<String, String>();

        parameterMap.put("Version", "2014-11-11");
        parameterMap.put("AccessKeyId", LonglianSsoUtil.accessKeyId);

        parameterMap.put("Timestamp", df.format(new Date()));
        parameterMap.put("SignatureMethod", "HMAC-SHA1");
        parameterMap.put("SignatureVersion", "1.0");
        parameterMap.put("SignatureNonce", UUID.randomUUID().toString());
        parameterMap.put("Format", "JSON");

        parameterMap.put("Action", "CreateLiveStreamRecordIndexFiles");
        parameterMap.put("DomainName", domain);
        parameterMap.put("AppName", appName);
        parameterMap.put("StreamName", streamName);
        parameterMap.put("OssEndpoint",   OssBucket.longlian_live2.getEndpoint());
        parameterMap.put("OssBucket", OssBucket.longlian_live2.getName());

        String dateStr = DateUtil.format(new Date(), "yyyy-MM-dd");
        parameterMap.put("OssObject", env + "/record/" + dateStr  + "/" + appName + "/"  + streamName +".m3u8");

        parameterMap.put("StartTime", start);
        parameterMap.put("EndTime", end);
        log.info("请求参数:{}" ,parameterMap);
        List<String> sortedKeys = new ArrayList<String>(parameterMap.keySet());
        Collections.sort(sortedKeys);

        final String SEPARATOR = "&";
        final String EQUAL = "=";
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(HTTP_METHOD).append(SEPARATOR);
        stringToSign.append(percentEncode("/")).append(SEPARATOR);
        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (String key : sortedKeys) {
            String value = parameterMap.get(key);
            canonicalizedQueryString.append(SEPARATOR).append(percentEncode(key)).append(EQUAL).append(percentEncode(value));

        }

        stringToSign.append(percentEncode(canonicalizedQueryString.toString().substring(1)));
        final String ALGORITHM = "HmacSHA1";
        final String secret = SsoUtil.accessKeySecret;

        SecretKey key = new SecretKeySpec((secret + SEPARATOR).getBytes(ENCODE_TYPE), SignatureMethod.HMAC_SHA1);
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(key);
        String signature = URLEncoder.encode(new String(new org.apache.commons.codec.binary.Base64().encode(mac.doFinal(stringToSign.toString().getBytes(ENCODE_TYPE))), ENCODE_TYPE), ENCODE_TYPE);

        StringBuilder requestURL;
        requestURL = new StringBuilder("https://cdn.aliyuncs.com?");
        requestURL.append(URLEncoder.encode("Signature", ENCODE_TYPE)).append("=").append(signature);
        for (Map.Entry<String, String> e : parameterMap.entrySet()) {
            requestURL.append("&").append(percentEncode(e.getKey())).append("=").append(percentEncode(e.getValue()));
        }
        return requestURL.toString();

    }

    public static void main(String args[]) {
        try{
            String url = getRequestAddress(DateUtil.format("2017-05-19 14:52:00")
                    ,DateUtil.format("2017-05-19 15:36:25")
                    , "livedev.llkeji.com"
                    , "104"
                    , "1306" , "dev");
            String result = doGet(url);
            Map resMap = JsonUtil.getObject(result , HashMap.class);
            String code = (String)resMap.get("Code");
            if (code != null && !"".equals(code)) {
                log.info(code);
                return ;
            }
            Map info = (Map)resMap.get("RecordInfo");
            System.out.println((String)info.get("RecordUrl"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void updateConvert(String courseId) {

    }

    @Override
    public void create(String courseId) {

    }

    /**
     * 判断课程下视频流在七牛上是否存在
     *
     * @param courseId
     * @return
     */
    @Override
    public boolean getStreamIsExist(Long courseId) {
        return false;
    }
}
