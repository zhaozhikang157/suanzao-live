package com.longlian.live.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 初始化类
 */
public class InitUtil {

    public static Map initMap() {
        Map map = new HashMap();
        map.put("1" , "11");
        map.put("2" , "2");
        map.put("3" , "3");

        Map map2 = new HashMap();
        map.put("4" , map2);
        map2.put("41","222");
        map2.put("42","222");
        map2.put("43","222");

        Map map3 = new HashMap();
        map.put("5" , map3);
        map3.put("51","222");
        map3.put("52",new int[]{1,2,3});

        Map map4 = new HashMap();
        map3.put("53",map4);
        map4.put("531",new int[]{1,2,3});
        map4.put("532","");
        map4.put("533","444");

        return map;
    }

    public static Map<String, Integer> initNodes() {
        Map<String, Integer> nodes = new HashMap<>();
        nodes.put("A" , 1 );
        nodes.put("A" , 1 );
        nodes.put("B" , 1);
        nodes.put("C" , 1);
        nodes.put("D" , 1);
        nodes.put("E" , 1);
        nodes.put("F" , 1);
        return nodes;
    }

    public static List<Route> initRoutes() {
        List<Route> routes = new ArrayList<>();
        routes.add(new Route("A" , "B"));
        routes.add(new Route("B" , "C"));
        routes.add(new Route("C" , "E"));
        routes.add(new Route("C" , "D"));
        routes.add(new Route("A" , "E"));
        routes.add(new Route("B" , "D"));
        routes.add(new Route("B" , "E"));
        routes.add(new Route("C" , "F"));
        routes.add(new Route("E" , "D"));
        return routes;
    }
}
