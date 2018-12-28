package com.huaxin.util.db;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源 继承抽象路由自动切换
 * Created by syl on 2017/1/10.
 */
public class DynamicDataSource extends AbstractRoutingDataSource{
    private Logger log = Logger.getLogger(this.getClass());

    //是否开始主从服务器数据
    public  final  static  boolean isUseMasterSlave = true;

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = null;
        if(isUseMasterSlave){
            dataSourceName = DynamicDataSourceKeyHolder.getDataSourceKey();
            logger.info("determineCurrentLookupKey:" + dataSourceName);
            DynamicDataSourceKeyHolder.clearDataSourceKey();
        }
        return dataSourceName;
    }

}
