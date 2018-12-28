package com.longlian.live.util;

/**
 * Created by liuhan on 2017-12-26.
 * 路径,包括当前结点，以及能到达的结点
 */
public class Route {
    public String node;
    public String nextNode;

    public Route(String node, String nextNode) {
        this.node = node;
        this.nextNode = nextNode;
    }

    public String getNode() {
        return  node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getNextNode() {
        return nextNode;
    }

    public void setNextName(String nextNode) {
        this.nextNode = nextNode;
    }
}
