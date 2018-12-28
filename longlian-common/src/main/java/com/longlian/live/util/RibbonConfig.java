package com.longlian.live.util;

import com.longlian.live.third.service.AppMsgRemote;
import com.longlian.live.third.service.ChatMsgRemote;
import com.netflix.client.ClientFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.*;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.ribbon.LBClient;
import feign.ribbon.LBClientFactory;
import feign.ribbon.RibbonClient;

import java.io.IOException;

/**
 * Created by liuhan on 2018-02-28.
 */
public class RibbonConfig {
    static {
        try {
            ConfigurationManager.loadPropertiesFromResources("client.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ChatMsgRemote chatMsgRemote() {
        RibbonClient client = getClient();
        ChatMsgRemote service = Feign.builder()
                .client(client)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder()).target(ChatMsgRemote.class, "http://chatmsg");
        return service;
    }

    public static AppMsgRemote appMsgRemote() {
        RibbonClient client = getClient();
        AppMsgRemote service = Feign.builder()
                .client(client)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder()).target(AppMsgRemote.class, "http://appmsg");
        return service;
    }


    public static RibbonClient getClient() {
        RibbonClient client = RibbonClient.builder().lbClientFactory(new LBClientFactory() {
            @Override
            public LBClient create(String clientName) {
                IClientConfig config = ClientFactory.getNamedConfig(clientName);
                ILoadBalancer lb = ClientFactory.getNamedLoadBalancer(clientName);
                ZoneAwareLoadBalancer zb = (ZoneAwareLoadBalancer) lb;
                zb.setRule(randomRule());
                return LBClient.create(zb, config);
            }
        }).build();
        return client;
    }

    /**
     * Ribbon负载均衡策略实现
     * 使用ZoneAvoidancePredicate和AvailabilityPredicate来判断是否选择某个server，前一个判断判定一个zone的运行性能是否可用，
     * 剔除不可用的zone（的所有server），AvailabilityPredicate用于过滤掉连接数过多的Server。
     * @return
     */
    private static IRule zoneAvoidanceRule() {
        return new ZoneAvoidanceRule();
    }

    /**
     * Ribbon负载均衡策略实现
     * 随机选择一个server。
     * @return
     */
    private static IRule randomRule() {
        return new RandomRule();
    }
}
