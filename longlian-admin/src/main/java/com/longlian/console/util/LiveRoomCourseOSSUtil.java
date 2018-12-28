package com.longlian.console.util;

import cn.jpush.api.utils.StringUtils;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.huaxin.util.type.CourseOSS;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * Created by longlian007 on 2018/1/29.
 * 课程OSS 操作
 */
@Service("liveRoomCourseOSSUtil")
public class LiveRoomCourseOSSUtil {
    //日志输出
    private static Logger log = LoggerFactory.getLogger(LiveRoomCourseOSSUtil.class);
    private static String accessKeyId="HdhrU64EnCRlKEmY";
    private static String accessKeySecret="82lNWjoyAUfTuhXfhcDN4rTWpZM8Ta";

    /**
     * 删除OSS-用于数据清除
     */
    public void delOSS(List<Course> courses){
        OSSClient ossClient = null;
        try {
            List<Map> coursesMap = this.getMapByType(courses);//根据课程集合进行分类
            if(coursesMap!=null&&coursesMap.size()>0){
                for(Map map : coursesMap){
                    String mapNameByKey = this.getMapNameByKey(map);
                    if(mapNameByKey.equals(CourseOSS.longlian_live.getText())){
                        ossClient = new OSSClient(CourseOSS.longlian_live.getText(),accessKeyId,accessKeySecret);
                        List<String> mapkeysByKey = this.getMapkeysByKey(map,ossClient);//获取 map中 keys 并set转list
                        if(mapkeysByKey!=null&&mapkeysByKey.size()>0){
                            this.delAllOssFile(ossClient, CourseOSS.longlian_live.getValue(), mapkeysByKey);
                        }
                    }
                    if(mapNameByKey.equals(CourseOSS.longlian_live2.getText())){
                        ossClient = new OSSClient(CourseOSS.longlian_live2.getText(),accessKeyId,accessKeySecret);
                        List<String> mapkeysByKey = this.getMapkeysByKey(map,ossClient);//获取 map中 keys 并set转list
                        if(mapkeysByKey!=null&&mapkeysByKey.size()>0){
                            this.delAllOssFile(ossClient, CourseOSS.longlian_live2.getValue(), mapkeysByKey);
                        }}
                    if(mapNameByKey.equals(CourseOSS.longlian_output.getText())){
                        ossClient = new OSSClient(CourseOSS.longlian_output.getText(),accessKeyId,accessKeySecret);
                        List<String> mapkeysByKey = this.getMapkeysByKey(map,ossClient);//获取 map中 keys 并set转list
                        if(mapkeysByKey!=null&&mapkeysByKey.size()>0){
                           this.delAllOssFile(ossClient, CourseOSS.longlian_output.getValue(), mapkeysByKey);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取OSS列表 暂定最大数据为1000
     * @param ossClient
     * @param bucKetName
     * @param diskName
     * @return
     */
    private  void setKeys(OSSClient ossClient,String bucKetName,String diskName,List<String> strlist){
        if(ossClient!=null){
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucKetName, diskName, null, null, 1000);
            ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
            List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            for (OSSObjectSummary oc : objectSummaries){
                strlist.add(oc.getKey());
            }
        }
    }

    /**
     * 关闭客户端
     * @param ossClient
     */
    public void close(OSSClient ossClient){
        if(ossClient != null){
            ossClient.shutdown();
        }
    }

    /**
     * 批量删除 一次最多删除1000条数据
     * @param ossClient
     * @param bucKetName
     * @param keys
     * @return
     */
    private void delAllOssFile(OSSClient ossClient,String bucKetName,List<String> keys){
       try{
           List<List<String>> listPage = this.getListPage(keys);//获取分页后的集合数据
           DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucKetName);
           for(List<String> key :listPage){
               DeleteObjectsRequest request =deleteObjectsRequest.withKeys(key);
               if(request!=null){
                   ossClient.deleteObjects(request);
               }
           }
       }catch (Exception e){
            e.printStackTrace();
       }finally {
           this.close(ossClient);
       }
    }

    /**
     * 把超过1000的集合拆分成每个最大数据量为1000的多个集合
     * @param keys
     * @return
     */
    private List<List<String>> getListPage(List<String> keys){
        List<List<String>> strlist = new ArrayList<List<String>>();
        if(keys!=null&&keys.size()<1001){
            strlist.add(keys);
        }else if(keys!=null&&keys.size()>1000){
            int i = 0;//计数使用
            List<String> s = new ArrayList<String>();
            for(String str : keys){
                s.add(str);
                i++;
                if(i==1000){
                    strlist.add(s);
                    s = new ArrayList<String>();
                    i=0;
                }
            }
            if(s!=null&&s.size()>0){
                strlist.add(s);
            }
        }
        return strlist;
    }

    /**
     * 对课程OSS进行分类 目前三类（对应三个bucket）
     * @param courses
     * @return
     */
    private List<Map> getMapByType(List<Course> courses){
        if(courses!=null&&courses.size()>0){
            List<Map> maps = new ArrayList<Map>();//创建一个空存储
            Map file = new HashMap();//存储 longlian_live
            file.put("endpointWrite", CourseOSS.longlian_live.getText());
            Map file2 = new HashMap();//存储 longlian_live2
            file2.put("endpointWrite", CourseOSS.longlian_live2.getText());
            Map file3 = new HashMap();//存储 longlian_output
            file3.put("endpointWrite",  CourseOSS.longlian_output.getText());

            //创建三个存储diskName的集合
            Set<String> diskNamesfile = new HashSet<String>();
            List<String> diskNamesfile2 = new ArrayList<String>();
            List<String> diskNamesfile3 = new ArrayList<String>();

            for(Course course : courses) {
                String videoAddress = course.getVideoAddress();
                if (course != null && StringUtils.isNotEmpty(videoAddress) && !"novideo".equals(videoAddress)) {
                        if(StringUtils.isNotEmpty(videoAddress)&&(videoAddress.contains(CourseOSS.longlian_live2.getText())||videoAddress.contains("longlian-live2.oss-cn-shanghai.aliyuncs.com"))){
                            this.setDiskNamesfile2(videoAddress, diskNamesfile2);
                        }else if(StringUtils.isNotEmpty(videoAddress)&&(videoAddress.contains(CourseOSS.longlian_output.getText())||videoAddress.contains("longlian-output.oss-cn-beijing.aliyuncs.com"))){
                            this.setDiskNamesfile3(videoAddress, diskNamesfile3);
                        }else{
                            this.setDiskNamesfile(videoAddress, diskNamesfile);
                        }
                }
            }
            file.put("diskNames", diskNamesfile);
            file2.put("diskNames", diskNamesfile2);
            file3.put("diskNames", diskNamesfile3);
            maps.add(file);
            maps.add(file2);
            maps.add(file3);
            return maps;
        }
        return null;
    }

    /**
     * 用来截取 videoAddress 获取 biskName
     * @param videoAddress
     * @return
     */
    private  String getEndpointWrite(String videoAddress){
       if(StringUtils.isNotEmpty(videoAddress)){
           String[] split = videoAddress.split(".com/");
           if (split != null && split.length == 2) {
              return split[1];
           }
       }
        return null;
    }

    /**
     * 设置 DiskNamesfile （longlian-live，）
     * @param videoAddress
     */
    private void setDiskNamesfile(String videoAddress, Set<String> diskNamesfile){
        if(StringUtils.isNotEmpty(videoAddress)){
            try{
                URL url = new URL(videoAddress);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String s;
                String split = this.getEndpointWrite(videoAddress);
                diskNamesfile.add(split);
                String[] split1 =split.split("/");
                String[] split2 =split.split(split1[split1.length - 1]);
                while ((s = reader.readLine()) != null) {
                    if(!s.contains("#")){
                        diskNamesfile.add(split2[0]+s);
                    }
                }
                reader.close();
            }catch (Exception e){
                log.info("网络流不存在或者已被清除："+videoAddress,e);
            }
        }
    }

    /**
     * 设置 DiskNamesfile （longlian-live3）
     * @param videoAddress
     */
    private void setDiskNamesfile2(String videoAddress, List<String> diskNamesfile){
        if(StringUtils.isNotEmpty(videoAddress)){
            String diskName = this.getEndpointWrite(videoAddress);
            diskNamesfile.add(diskName);
        }
    }

    /**
     * 设置 DiskNamesfile （longlian-live3）
     * @param videoAddress
     */
    private void setDiskNamesfile3(String videoAddress, List<String> diskNamesfile){
        if(StringUtils.isNotEmpty(videoAddress)){
            String diskName = this.getEndpointWrite(videoAddress);
            String[] split1 = diskName.split("/");
            String diskName2 = "";
            if(split1!=null&&split1.length>0){
                for(int i = 0;i<split1.length-1;i++){
                    diskName2+=split1[i]+"/";
                }
            }
            diskNamesfile.add(diskName2);
        }
    }
    /**
     * 获取 map中连接参数
     * @param map
     * @return
     */
    private String getMapNameByKey(Map map){
        if(map!=null&&map.get("endpointWrite")!=null){
            return map.get("endpointWrite").toString();
        }
        return "";
    }

    /**
     * 获取 map中 keys 并set转list
     * @param map
     * @return
     */
    private List<String> getMapkeysByKey(Map map,OSSClient ossClient){
        String mapNameByKey = this.getMapNameByKey(map);
        if(mapNameByKey.equals(CourseOSS.longlian_live.getText())){
            if(map!=null&&map.get("diskNames")!=null){
                Set<String> strset  = ( Set<String>)map.get("diskNames");
                if(strset!=null&&strset.size()>0){
                    List<String> strlist = new ArrayList<String>();
                    strlist.addAll(strset);
                    return strlist;
                }
            }
        }
        if(mapNameByKey.equals(CourseOSS.longlian_live2.getText())){
            if(map!=null&&map.get("diskNames")!=null){
                List<String> strset  = (  List<String>)map.get("diskNames");
                if(strset!=null&&strset.size()>0){
                    List<String> strlist = new ArrayList<String>();
                    for(String str:strset){
                        strlist.add(str);
                        String[] split1 = str.split(".m3u8");
                        if(split1!=null&&split1.length>0){
                            this.setKeys(ossClient, CourseOSS.longlian_live2.getValue(),  split1[0]+"/", strlist);
                        }
                    }
                    return strlist;
                }
            }
        }
        if(mapNameByKey.equals(CourseOSS.longlian_output.getText())){
            List<String> strset  = (  List<String>)map.get("diskNames");
            if(strset!=null&&strset.size()>0){
                List<String> strlist = new ArrayList<String>();
                for(String str:strset){
                    this.setKeys(ossClient, CourseOSS.longlian_output.getValue(),str, strlist);
                }
                return strlist;
            }
        }
        return null;
    }
}
