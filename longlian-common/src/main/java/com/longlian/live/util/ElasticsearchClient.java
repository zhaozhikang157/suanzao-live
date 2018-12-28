package com.longlian.live.util;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

/**
 * Created by liuhan on 2017-07-03.
 */
@Service
public class ElasticsearchClient implements InitializingBean {

    @Value("${elasticsearch.cluster.name:elasticsearch}")
    public  String elasticsearchClusterName;
    @Value("${elasticsearch.esNodes.host:}")
    public  String host;
    @Value("${elasticsearch.esNodes.port:9300}")
    public  int port;
    @Value("${elasticsearch.index:longlian}")
    public String index;

    private TransportClient client;

    public TransportClient getInstance() {
        return client;
    }

    private Logger logg = LoggerFactory.getLogger(ElasticsearchClient.class);

    public String getIndex() {
        return index;
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception in the event of misconfiguration (such
     *                   as failure to set an essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            Settings settings = Settings.builder( )
                    .put("cluster.name", elasticsearchClusterName).build();
            client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port) );
            if (client == null) {
                logg.error("未连接上:{}:{},{}" , host , port, elasticsearchClusterName);
            } else {
                logg.info("已连接上:{}:{},{}" , host , port, elasticsearchClusterName);
            }
        } catch (Exception ex) {
            logg.error("未连接上:{}:{},{}" , host , port, elasticsearchClusterName);
            logg.error("报错：",ex);
        }

    }
}
