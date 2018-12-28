package com.longlian.live.util;

import com.huaxin.exception.GlobalExceptionHandler;
import com.longlian.live.util.yunxin.YunxinUserUtil;
import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuhan on 2017-06-23.
 */
public class HttpClientManage {

    private static Logger log = LoggerFactory.getLogger(HttpClientManage.class);


    private static PoolingHttpClientConnectionManager manager = null;
    private static CloseableHttpClient httpClient = null;

    public static synchronized CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            //注册访问协议相关的Socket工长
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http" , PlainConnectionSocketFactory.INSTANCE)
                    .register("https", SSLConnectionSocketFactory.getSystemSocketFactory())
                    .build();
            //HttpConnection工厂：配置写请求、解析响应处理器
            HttpConnectionFactory<HttpRoute , ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(DefaultHttpRequestWriterFactory.INSTANCE
                    , DefaultHttpResponseParserFactory.INSTANCE);

            //DNS解析器
            DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
            //创建池化连接管理器
            manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry , connFactory , dnsResolver);
            //默认socket配置
            SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();

            manager.setDefaultSocketConfig(defaultSocketConfig);
            //设置整个连接池的最大连接数
            manager.setMaxTotal(300);
            //每个中山路由的默认最大连接，每个路由实际最大连接数默认为DefaultMaxPerRoute控制，而MaxTotal是控制整个池子最大数
            //设置过小无法支持大并发(ConnectionPoolTimeoutException:Timeout waiting for connection from pool) , 路由是对maxTotal的细分
            manager.setDefaultMaxPerRoute(200);//每个路由最大连接数
            //在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证， 默认为2s
            manager.setValidateAfterInactivity(5 * 1000);

            //默认请求配置
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setConnectTimeout(10 * 1000) //设置连接超时时间 ， 10s
                    .setSocketTimeout(20 *1000)//设置等待数据超时时间 ， 20s
                    .setConnectionRequestTimeout(20000)//设置从连接池获取连接的待会等待超时时间
                    .build();

            httpClient = HttpClients.custom()
                    .setConnectionManager(manager)
                    .setConnectionManagerShared(false) //连接池不是共享模式
                    .evictIdleConnections(60 , TimeUnit.SECONDS) //定期回收空闲连接
                    .evictExpiredConnections()//定期回收过期连接
                    .setConnectionTimeToLive(60 , TimeUnit.SECONDS)//连接存活时间，如果不设置，则根据长连接信息决定
                    .setDefaultRequestConfig(defaultRequestConfig)//设置默认请求配置

                    .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)//连接重用策略，即获取长连接生产多长时间
                    .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)//设置重试次数， 默认是3次，当前是禁用掉（根据需要开启）
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0 , false))
                    .build();

            //JVM停止或重启时， 关闭连接是放掉连接（跟数据库连接池类似）
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    try {
                        httpClient.close();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return httpClient;
    }


    public static void dealException( HttpResponse response , Exception e , String tip) {
        if (response != null) {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException ex) {
                log.error(tip,ex);
            }
        }
        GlobalExceptionHandler.sendEmail(e , tip);
    }
}
