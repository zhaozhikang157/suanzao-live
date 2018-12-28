package com.longlian.live.service.impl;

import com.google.gson.Gson;
import com.huaxin.util.*;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.EndLiveService;
import com.longlian.live.service.LiveChannelService;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import com.longlian.model.LiveChannel;
import com.longlian.type.YunxinCustomMsgType;
import com.qiniu.pili.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 通道
 * Created by liuhan on 2018-03-09.
 */
@Service("qiNiuliveChannelService")
public class QiNiuLiveChannelServiceImpl implements LiveChannelService {
    private static Logger log = LoggerFactory.getLogger(QiNiuLiveChannelServiceImpl.class);

    static String accessKey = "QYj88DhzAGGKMuFGwapciTVV8YjLIWjVu-g-XSuv";
    static String secretKey = "C56Wz3q0LoJ9XsjQh8qbZs856OzxthR4bQyF6xsu";
    private Gson gson = new Gson();
    private static Client cli = new Client(accessKey,secretKey);
    @Autowired
    private RedisUtil redisUtil;
    //private final long exTimeIng = 300000l;     //直播中 判断掐流时间  5分钟 单位:毫秒
    private final long exTimeIng = 1 * 60 * 1000;     //直播中 判断掐流时间  修改为2分钟 单位:毫秒
    @Autowired
    private YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    private CourseBaseService courseBaseService;
    @Value("${qi-niu-live.pushAddress:}")
    private String pushAddress;
    @Value("${qi-niu-live.playAddressRTMP:}")
    private String playAddressRTMP  ;
    @Value("${qi-niu-live.playAddressFLV:}")
    private String playAddressFLV  ;
    @Value("${qi-niu-live.playAddressM3U8:}")
    private String playAddressM3U8  ;
    @Value("${qi-niu-live.domain:}")
    private String domain;
    @Value("${qi-niu-live.hub:}")
    private String hubName ;

    public void forbidLiveStream(LiveChannel liveChannel, Course course) throws Exception {
        Stream streamA;
        try{
            //取到直播空间
            Hub hub = cli.newHub(hubName);
            //取到直播流
            streamA = hub.get(String.valueOf(course.getId()));
            //禁用直播流
            streamA.disable();
        }catch (PiliException e){
            e.printStackTrace();
            log.error("停用流失败！{}",course.getId(),e);
            throw  e;
        }
    }

    public void resumeLiveStream(LiveChannel liveChannel, Course course) throws Exception {
        Stream streamA;
        try{
            //取到直播空间
            Hub hub = cli.newHub(hubName);
            //取到直播流
            streamA = hub.get(String.valueOf(course.getId()));
            //恢复直播流
            streamA.enable();
        }catch (PiliException e){
            e.printStackTrace();
            log.error("启用流失败！{}", course.getId(), e);
            throw  e;
        }
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
            String streamKey = String.valueOf(course.getId());
            //推流地址
            liveChannel.setPushAddr(getPushAddress("" , null ,streamKey) );
            String convert = courseBaseService.getConvert(course.getId());
            String s = cli.RTMPPlayURL(playAddressRTMP, hubName, streamKey);
            if(StringUtils.isNotEmpty(convert)){
                s=s+convert;
            }
            liveChannel.setPlayAddr1(s);
            liveChannel.setPlayAddr2(cli.HDLPlayURL(playAddressFLV, hubName, streamKey));
            liveChannel.setPlayAddr3(cli.HLSPlayURL(playAddressM3U8, hubName, streamKey));
        }
        return liveChannel;
    }

    @Override
    public String getPushAddress(String originUrl , LiveChannel liveChannel , String courseId) {
        return cli.RTMPPublishURL(pushAddress, hubName, courseId, 12 * 3600);
    }

    @Override
    public String getPlayAddress(String roomId, String courseId, LiveChannel liveChannel) {
        return cli.RTMPPlayURL(playAddressRTMP, hubName, courseId);
    }

    @Override
    public void updateConvert(String courseId) {
        try{
            log.info("开始配置转码==========================================Hub:"+hubName+"ID:"+courseId);
            cli.updateConverts(hubName,courseId);
            log.info("配置转码结束==========================================");
        }catch (Exception e){
            log.info("修改转码配置", e);
        }
    }

    @Override
    public void create(String courseId) {
        try{
            log.info("开始创建流==========================================ID:"+courseId);
            Hub hub = cli.newHub(hubName);
            Stream stream = hub.create(courseId);
            log.info("创建流结束==========================================");
            log.info("开始配置转码==========================================Hub:"+hubName+"ID:"+courseId);
            List<String> cs = courseBaseService.getVideoConverts();
            String[] strs = null ;
            if(cs != null && cs.size() > 0){
                String[] s = new String[cs.size()];
                strs = cs.toArray(s);
            }
            log.info("视频转码类型："+Arrays.toString(strs));
            stream.updateConverts(strs);
            log.info("配置转码结束==========================================");
        }catch (Exception e){
            log.info("创建流配置转码", e);
        }
    }

    @Override
    public void saveRecord(Course course , String domain, String env) {
        course.setVideoAddress(getVideoAddr(course ));
        courseBaseService.updateVideoAddr(course);
    }

    @Override
    public ActResultDto liveNotify(String courseId , String action ) {
        Course course = courseBaseService.getCourseFromRedis(Long.parseLong(courseId));
        if (course == null ) {
            log.error("未找到对应的课程");
            return ActResultDto.success();
        }
        String ac = "1".equals(action) ? "1" : "0";
        //直播流连接,则发送mq,修改course中的isConnection的状态，让直播在正在直播中显示,这里有点问题
        if("1".equals(ac)){
            Map map = new HashMap();
            map.put("courseId",courseId);
            map.put("isConnection",1);
            redisUtil.lpush(RedisKey.course_is_connection, JsonUtil.toJson(map));
        }
        //提供数据给接口：live/isConnected.user直播流状态
        redisUtil.set(RedisKey.ll_course_live_connected  + courseId , ac);
        //是否有正在直播 -- 用于 直播中途断开,没有重新连接
        redisUtil.set(RedisKey.ll_live_notify_cation + courseId, ac);
        redisUtil.expire(RedisKey.ll_live_notify_cation + courseId , 24 * 60 * 60);
        //直播流断开
        if("0".equals(ac)){
            String redisKey = RedisKey.ll_course_live_notify_cation + course.getId();
            setTimer(Long.parseLong(courseId), redisKey, "1", null, exTimeIng);
        }
        //课程已经结束了就不发消息
        String isEnd = redisUtil.get(RedisKey.course_is_end + courseId);
        if ("1".equals(isEnd)) {
            return ActResultDto.success();
        }
        Map msg = new HashMap();
        //直播流断开 publish_done ,直播流愎复 connected
        msg.put("type","1".equals(action) ? YunxinCustomMsgType.LIVE_CHANNEL_OPEN.getType() : YunxinCustomMsgType.LIVE_CHANNEL_CLOSE.getType());
        Map val = new HashMap();
        val.put("value","1".equals(action) ? "直播流接通了" : "直播流断开了");
        msg.put("data",val);
        //给相关的聊天室人员发送一个直播流断开(恢复) 的消息
        log.info(JsonUtil.toJson(msg));
        yunxinChatRoomUtil.sendMsg(String.valueOf(course.getChatRoomId()), String.valueOf(course.getAppId()), "100", JsonUtil.toJson(msg));
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
    public Map<String , String> getRoomToken(Long courseId, Long userId) throws Exception {
            //Course course = courseBaseService.getCourseFromRedis(courseId);
            Map result = new HashMap();
            //不存在房间
            String key = RedisKey.connect_room  + courseId;
            String roomName = redisUtil.get(key);
            Meeting meeting = cli.newMeeting();
            if (StringUtils.isEmpty(roomName) ) {
                try {

                     roomName = hubName + "_" + String.valueOf(courseId);
                    roomName = meeting.createRoom("admin", roomName);
                    System.out.println( "====================" + roomName);
                }catch (PiliException e){
                    if (e.code() == 611) {
                        log.error("房间已存在，{}" , roomName);
                    } else {
                        log.error("传入参数有问题，{}" , e);
                        throw e;
                    }
                }
                //缓存房间名
                redisUtil.set( key , roomName);
                //课程结束会删除房间名
            }

//            RoomName: 房间名称，需满足规格^[a-zA-Z0-9_-]{3,64}$
//            UserID: 请求加入房间的用户ID，需满足规格^[a-zA-Z0-9_-]{3,50}$
//            Permission: 该用户的房间管理权限，"admin"或"user"，房间主播为"admin"，拥有将其他用户移除出房间等特权。
//            ExpireAt: int64类型，鉴权的有效时间，传入以秒为单位的64位Unix绝对时间，token将在该时间后失效。
            String per =  "admin" ;

            String roomUserId  = hubName + "_"  + userId;
            // 3. 将AccessKey与以上两者拼接得到房间鉴权
            result.put("roomName" , roomName);
            result.put("userId" , roomUserId);
            //String roomName, String userId, String perm, Date expireAt
            //token一个小时过期
            String token =  meeting.roomToken(roomName ,roomUserId,per, new Date(new Date().getTime() + 3600 * 1000L));
            result.put("roomToken" ,token);
            return result;
    }

    /**
     * 删除课程room
     *
     * @param courseId
     * @return
     */
    @Override
    public void delRoom(Long courseId) {
        String roomName = hubName +"_" + courseId;
        try {
            String key = RedisKey.connect_room + ":" + courseId;
            redisUtil.del(key);

            Meeting meeting = cli.newMeeting();
            meeting.deleteRoom(roomName);
        }catch (PiliException e){
            if (e.code() == 612) {
                log.error("房间不存在，{}"  , roomName);
            } if (e.code() == 613) {
                log.error("房间正在使用不能删除，{}" , roomName);
            }
            e.printStackTrace();
        }
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

    private String getVideoAddr(Course course)  {
        //保存直播数据
        String fname = null;
        try {
            Hub hub = cli.newHub(hubName);
            Stream  stream = hub.get(String.valueOf(course.getId()));
            fname = stream.save(0,0);
            return "http://" + domain + "/" + fname;
        }catch (PiliException e){
            if (!e.isNotInLive()) {
                e.printStackTrace();
                String tip = "视频直播:" + course.getId() +"," + course.getLiveTopic() +"未取到直播地址，请注意！";
                log.error("{}",tip,e);
                String receive = CustomizedPropertyConfigurer.getContextProperty("exception.receive");
                if (!Utility.isNullorEmpty(receive)) {
                    String[] res = receive.split(",");
                    EmailUtil.getInstance().send(IPUtil.getLocalIp4()+"-mq错误",tip, res);
                }
            }else{
                //未录制
                return "novideo";
            }
        }
        return "novideo";
    }

    /**
     * 判断课程下视频流在七牛上是否存在
     * @param courseId
     * @return
     */
    public boolean getStreamIsExist(Long courseId) {
        try {
            Hub hub = cli.newHub("livedev");
            Stream  stream = hub.get(String.valueOf(courseId));
            Stream info = stream.info();
            String key = info.getKey();
            String[] converts = info.getConverts();
            if(StringUtils.isNotBlank(key) && converts != null && converts.length >= 1){
                return true;
            }
        }catch (PiliException e){
            if (!e.isNotInLive()) {
                e.printStackTrace();
                String tip = "判断课程是否有流文件存在:" + courseId ;
                log.error("{}",tip,e);
            }
            return false;
        }
        return false;
    }
    /*public static void main(String[] args){
        try {
            Hub hub = cli.newHub("livedev");
            Stream stream = hub.get(String.valueOf(14255));
            stream.updateConverts(new String[]{"480p","720p"});
            System.out.printf("sucessful!!!");
        } catch (PiliException e) {
            e.printStackTrace();
        } catch (Exception ee){
            ee.printStackTrace();
        }
    }*/
    /*public static void main(String args[]) {
        Meeting meeting = cli.newMeeting();
        String roomName =  "10000";
        //https://developer.qiniu.com/pili/sdk/1640/server-rtc-sdk
        //https://github.com/pili-engineering/PLRTCStreamingKit/blob/master/Documents/Qiniu-RTC-Server-API-v2.md
        //生成房间
        try {
            String name = meeting.createRoom("123" , roomName , 3);
            System.out.println( "====================" + name);
        }catch (PiliException e){
            e.printStackTrace();
        }
        //取得房间
        try {
            Meeting.Room room =   meeting.getRoom(roomName);
            System.out.println(JsonUtil.toJson(room));
        }catch (PiliException e){
            e.printStackTrace();
        }

        //生成roomtoken
        try {
            String userId = "llkeji-" + UUID.randomUUID().toString();
            Date date = DateUtil.format("2018-03-22 13:59:01");
            String token =  meeting.roomToken(roomName ,userId,"admin",  date);
            System.out.println(token);
            System.out.println(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
