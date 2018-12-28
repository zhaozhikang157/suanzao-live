package com.longlian.live.util;

import com.alibaba.fastjson.JSONObject;
import com.huaxin.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by liuhan on 2017-12-26.
 */
public class Main {
    public static void main(String[] args) {
        //第一题Json格式转换
        System.out.println(getJson(JsonUtil.toJsonString(InitUtil.initMap()))) ;
        //第二题数据存取
        String text = "key1=value1;key2=value2;key3=value3\nkeyA=value;keyA2=value2\nkeyB=value";
        Map<String , String>[] maps = load(text);
        System.out.println(store(maps));
        //路径规划
        findMaxPath( InitUtil.initNodes()  ,  InitUtil.initRoutes(), "A");
    }



    /**
     * 转化字符串到json,并分析
     * @param json
     * @return
     */
    public static String getJson(String json) {
        JSONObject parent = JsonUtil.getObject(json);
        JSONObject convertResult = new JSONObject();
        loadJson("" , parent , convertResult);
        return convertResult.toString();
    }

    /**
     * 递归json，转出结果到json
     * @param keys
     * @param obj
     * @param convertResult
     */
    public static  void loadJson(String keys , JSONObject obj , JSONObject convertResult) {
        Set<String> its = obj.keySet();
        for (String key : its) {
            Object child = obj.get(key);
            //如果是jsonObject
            if (child instanceof JSONObject) {
                 loadJson(keys + key + "." , (JSONObject)child , convertResult);
             //如果是值
            } else {
                String childkey = keys + key ;
                convertResult.put(childkey , child);
            }
        }
    }

    /**
     * 数组存储到字符串
     * @param maps
     * @return
     */
    public static String store(Map<String , String >[] maps) {
        StringBuffer sb = new StringBuffer();
        for ( Map<String , String> map : maps) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
               sb.append(key).append("=").append(map.get(key)).append(";");
            }
            if (keys.size() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("\n");
        }
        if (maps.length > 0 ) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 字符串加载到数组
     * @param text
     * @return
     */
    public static Map<String , String>[] load(String text ) {
        //按行截取
        StringTokenizer lineTokenizer = new StringTokenizer(text , "\n");
        Map<String , String>[]  result = new HashMap[lineTokenizer.countTokens()];
        int index = 0 ;
        while (lineTokenizer.hasMoreElements()) {
            String line =  lineTokenizer.nextToken();
            //按";"截取
            StringTokenizer keyTokenizer = new StringTokenizer(line , ";");
            //组装map
            Map<String , String> map = new HashMap<>();
            while (keyTokenizer.hasMoreElements()) {
                String node =  keyTokenizer.nextToken();
                String key = node.substring(0 , node.indexOf("="));
                String value = node.substring(node.indexOf("=") + 1);
                map.put(key , value);

            }
            result[index] = map;
            index++;
        }
        return result;
    }


    /**
     * 输出最大值
     * @param nodes
     * @param routes
     * @param startNode
     * @return
     */
    public static Integer findMaxPath(Map<String, Integer> nodes  , List<Route> routes, String startNode) {
        Path path = new Path(startNode , nodes.get(startNode));
        List<Path> paths = new ArrayList<>();
        try {
            findPath( nodes ,   routes,   path , paths);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        Collections.sort(paths, new Comparator<Path>() {
            @Override
            public int compare(Path o1, Path o2) {
                return   o2.getWeightCount() - o1.getWeightCount();
            }
        });

        for (Path temp : paths) {
            System.out.println(temp.toString());
        }

        return paths.size() > 0 ? paths.get(0).getWeightCount() : 0;
    }

    /**
     * 递归查找路径
     * @param nodes
     * @param routes
     * @param path
     * @param paths
     * @throws Exception
     */
    public static void findPath(Map<String, Integer> nodes , List<Route> routes, Path path , List<Path> paths) throws Exception{
        String endNode = path.getEndNode();
        boolean isLeaf = true;
        for (Route route : routes) {
            if (route.getNode().equals(endNode)) {
                isLeaf = false;
                Path oldPath = path.newPath();
                //加上路径
                if (!path.addRotes(route ,nodes.get(route.getNextNode()))) {
                    throw new Exception("有环图");
                }
                //找下一路径
                findPath( nodes , routes,   path , paths);
                //只要上面的递归返回，说明下面要走的就是新路径
                path = oldPath;
            }
            continue;
        }
        //没有找到下面的路径时，说明是叶子结点
        if (isLeaf) {
            paths.add(path);
        }
    }

}
