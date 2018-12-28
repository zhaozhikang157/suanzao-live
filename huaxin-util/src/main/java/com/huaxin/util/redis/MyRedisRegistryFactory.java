package com.huaxin.util.redis;


import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.RegistryFactory;

public class MyRedisRegistryFactory  implements RegistryFactory {
        public Registry getRegistry(URL url) {
            return new MyRedisRegistry(url);
        }

    }
