package com.longlian.mq.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.db.DataSource;
import com.huaxin.util.db.DynamicDataSourceKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.exception.MobileGlobalExceptionHandler;
import com.longlian.live.dao.CdnVisitMapper;
import com.longlian.live.util.log.Log;
import com.longlian.live.util.HttpClientManage;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.live.util.SignatureUtils;
import com.longlian.model.CdnVisit;
import com.longlian.mq.service.CdnVisitService;
import com.longlian.mq.service.LonglianLogService;
import com.longlian.mq.service.ResolveVisitLogService;
import com.longlian.mq.util.LogDealUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by liuhan on 2017-10-10.
 */
@Service("cdnLogService")
public class CdnLogServiceImpl implements LonglianLogService {

    @Value("${live.domain:livedev.llkeji.com}")
    private String domain = "livedev.llkeji.com";

    private static Logger log = LoggerFactory.getLogger(CdnLogServiceImpl.class);

    @Autowired
    CdnVisitMapper cdnVisitMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CdnVisitService cdnVisitService;


    /**
     * 根据时间请求日志
     * @param logDate
     */
    private Long getLogFilePath(String logDate ,Long pageSize,Long pageNumber , List<String> result) {
        //https://cdn.aliyuncs.com?Action=DescribeCdnDomainLogs&DomainName=gc.ggter.com&LogDay=2015-05-24&<公共请求参数>
        HttpResponse response = null;
        long i = 0;
        try {
            String url = "https://cdn.aliyuncs.com?";

            String app_key="HdhrU64EnCRlKEmY";
            String Format="JSON";
            String Version="2014-11-11";
            String SignatureMethod="HMAC-SHA1";
            String SignatureVersion="1.0";
            String Action="DescribeCdnDomainLogs";

            Map<String,String> param = new HashMap<String,String>();
            param.put("AccessKeyId", app_key);
            param.put("Format", Format);
            param.put("Version", Version);
            param.put("SignatureMethod", SignatureMethod);
            param.put("SignatureVersion", SignatureVersion);
            param.put("SignatureNonce", UUID.randomUUID().toString());
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(tz);
            String date =  df.format(new Date());

           // System.out.println(date);
            param.put("Timestamp", date);
            param.put("Action", Action);
            param.put("LogDay" ,logDate );
            param.put("DomainName" ,domain);
            param.put("PageSize" ,pageSize+"");
            param.put("PageNumber" ,pageNumber+"" );
            try {
                String signature = SignatureUtils.generate("GET",param, LonglianSsoUtil.accessKeySecret);
                param.put("Signature" ,signature );
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (String key : param.keySet()) {
                url += key + "=" + param.get(key) + "&";
            }

            url = url.substring(0 , url.length() - 1);
            //System.out.println(url);
            HttpGet httpGet = new HttpGet(url);

            response = HttpClientManage.getHttpClient().execute(httpGet);
            int status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.SC_OK) {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
            } else {
                String str = EntityUtils.toString(response.getEntity(), "utf-8");
                Map resMap = JsonUtil.getObject(str , HashMap.class);
                Map DomainLogDetails = (Map)((Map)resMap.get("DomainLogModel")).get("DomainLogDetails");
                List<Map> DomainLogDetail = (List)DomainLogDetails.get("DomainLogDetail");
                i = Long.parseLong(resMap.get("TotalCount").toString());
                for (Map map : DomainLogDetail ) {
                    String path = "https://" + (String) map.get("LogPath");
                    String fileName = (String) map.get("LogName");
                    result.add(path);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            HttpClientManage.dealException(response , e , "");
        }
        return i;
    }

    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @throws IOException
     */
    private List<String> downLoadFromUrl(String urlStr) {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            log.info("下载日志文件：{}" , urlStr);
            url = new URL(urlStr);
            conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            inputStream = conn.getInputStream();
            return unGzipFile(inputStream);
        } catch (MalformedURLException e) {
            log.error("下载日志出错：" ,e);
            MobileGlobalExceptionHandler.sendEmail(e , "下载日志时出错:" + urlStr );
        } catch (IOException e) {
            log.error("下载日志出错：" ,e);
            MobileGlobalExceptionHandler.sendEmail(e , "下载日志时出错:" + urlStr );
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ArrayList<>();
    }

    private List<String> unGzipFile(InputStream inputStream) {
        GZIPInputStream in = null;
        List<String> list = new ArrayList<>();
        BufferedReader br = null;
        try {
            in = new GZIPInputStream(inputStream);
            br = new BufferedReader(new InputStreamReader(in,"utf8"));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                list.add(s);
            }
        } catch (IOException e) {
            log.error("解压日志出错：" ,e);
            MobileGlobalExceptionHandler.sendEmail(e , "解压日志出错" );
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }




    @Override
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public void resolveAndSave(ResolveVisitLogService resolveDetailService) {
        String date = LogDealUtil.getBeferDay();
        List<String> filePaths = new ArrayList<>();
        long totalCount = 0;
        long pageNumber = 1;
        long totalPage = 1;
        long pageSize =300;
        do {
            totalCount = this.getLogFilePath(date, pageSize, pageNumber , filePaths); //总记录数
            totalPage = totalCount / pageSize + ((totalCount % pageSize == 0) ? 0 : 1);
            pageNumber++;
        } while (pageNumber <= totalPage);
        List<String> logs = new ArrayList<>();
        for (String fileUrl : filePaths) {
             List<String> list = this.downLoadFromUrl(fileUrl);
            logs.addAll(list);
        }
        //Map<Long , Long> map = new HashMap<>();
        List<CdnVisit> list = new ArrayList<>();
        for (String logStr : logs) {
            CdnVisit r = null;
            try {
                r = resolveDetailService.getLiveCdvisit(logStr);
            } catch (Exception ex) {
                log.info("解析流量日志时出错：{} " , logStr);
                log.error("解析流量日志时出错： " , ex);
                MobileGlobalExceptionHandler.sendEmail(ex , "解析流量日志时出错");
            }
            if (r == null) continue;
            //System.out.println(JsonUtil.toJson(r));
            //System.out.println("请求地址：" + r.getUrl() + ",大小：" + r.getResponseSize() + ",courseId:" + r.getCourseId() + ",roomId:" + r.getRoomId());
            try {
                cdnVisitService.saveCdn(r , RedisKey.ll_cdn_log_live_room_id + date , RedisKey.ll_cdn_log_course_id + date, date);
            } catch (Exception ex) {
                log.info("统计流量时出错：{} " , JsonUtil.toJson(r));
                log.error("统计流量时出错： " , ex);
                MobileGlobalExceptionHandler.sendEmail(ex , "统计流量时出错");
            }
//                Long data = map.get(r.getRoomId());
//                if (data == null) {
//                    data = 0L;
//                }
//                data += r.getResponseSize();
//                map.put(r.getRoomId() , data);

            r = null;
        }

//        for (Long l : map.keySet()) {
//            System.out.println(l + ":" + map.get(l) / (1024 * 1024 *1024) + ":" + map.get(l) );
//        }
    }


    public static void main(String[] args) {
        LonglianLogService impl = new CdnLogServiceImpl();
        ResolveVisitLogService resolve = new CdnLogResolveImpl();
        impl.resolveAndSave(resolve);
    }

}
