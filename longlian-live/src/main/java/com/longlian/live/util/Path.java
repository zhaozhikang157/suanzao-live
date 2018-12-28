package com.longlian.live.util;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by liuhan on 2017-12-26.
 * 一条路径，包括所经过的结点、权重合计
 */
public class Path   {

    private Set<String> nodes  = new LinkedHashSet();

    private String endNode = null;

    private Integer weightCount;

    public Path( String startNode, Integer weightCount) {
        this.endNode = startNode;
        this.weightCount = weightCount;
        nodes.add(startNode);
    }


    public boolean addRotes(Route route , Integer nextWeight) {
        weightCount += nextWeight;
        endNode = route.getNextNode();
        return nodes.add(route.getNextNode());
    }

    public String getEndNode() {
        return endNode;
    }

    public Integer getWeightCount() {
        return weightCount;
    }

    public Path newPath() {
        Path path = new Path( this.endNode , this.weightCount);
        path.nodes  = new LinkedHashSet();
        for (String node : nodes) {
            path.nodes.add(node);
        }
        return path;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("路径：");
        for (String node : nodes) {
            sb.append(node).append("->");
        }

        if (nodes.size() > 0 ) {
            sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
        }

        sb.append(",权重：").append(this.weightCount);
        return sb.toString();
    }
}
