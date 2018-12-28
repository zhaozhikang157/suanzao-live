package com.longlian.chatmsg.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liuhan
 * @since 2017/2/5.
 */
@Configuration
public class DruidAutoConfiguration {

    @Value("${spring.datasource.initialSize}")
    private int defaultInitialSize = 1;
    @Value("${spring.datasource.minIdle}")
    private int defaultMinIdle = 1;
    @Value("${spring.datasource.maxActive}")
    private int defaultMaxActive = 20;
    @Value("${spring.datasource.testOnBorrow}")
    private boolean defaultTestOnBorrow = false;

    @Value("${spring.datasource.ds_0.url}")
    private String url0 = "";
    @Value("${spring.datasource.ds_0.username}")
    private String username0 = "";
    @Value("${spring.datasource.ds_0.password}")
    private String password0 = "";

    @Value("${spring.datasource.ds_1.url}")
    private String url1 = "";
    @Value("${spring.datasource.ds_1.username}")
    private String username1 = "";
    @Value("${spring.datasource.ds_1.password}")
    private String password1 = "";


    private void setDefaultProp(DruidDataSource dataSource) {
        if (defaultInitialSize > 0) {
            dataSource.setInitialSize(defaultInitialSize);
        }
        if (defaultMinIdle > 0) {
            dataSource.setMinIdle(defaultMinIdle);
        }
        if (defaultMaxActive > 0) {
            dataSource.setMaxActive(defaultMaxActive);
        }
        dataSource.setTestOnBorrow(defaultTestOnBorrow);
    }

    public DataSource dataSource0() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url0);
        dataSource.setUsername(username0);
        dataSource.setPassword(password0);
        setDefaultProp( dataSource);
        try {
            dataSource.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dataSource;
    }

    public DataSource dataSource1() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url1);
        dataSource.setUsername(username1);
        dataSource.setPassword(password1);
        setDefaultProp( dataSource);
        try {
            dataSource.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dataSource;
    }

    @Bean
    @ConditionalOnClass(DruidDataSource.class)
    public DataSource getDataSource() throws SQLException {
        return buildDataSource();
    }

    private DataSource buildDataSource() throws SQLException {
        //设置分库映射
        Map<String, DataSource> dataSourceMap = new LinkedHashMap<>(2);
        //添加两个数据库ds_0,ds_1到map里
        dataSourceMap.put("ds_0", dataSource0());
        dataSourceMap.put("ds_1", dataSource1());
        //设置默认db为ds_0，也就是为那些没有配置分库分表策略的指定的默认库
        //如果只有一个库，也就是不需要分库的话，map里只放一个映射就行了，只有一个库时不需要指定默认库，但2个及以上时必须指定默认库，否则那些没有配置策略的表将无法操作数据
        DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap, "ds_0");

        //设置分表映射，将t_order_0和t_order_1两个实际的表映射到t_order逻辑表
        //0和1两个表是真实的表，t_order是个虚拟不存在的表，只是供使用。如查询所有数据就是select * from t_order就能查完0和1表的
        TableRule orderTableRule = TableRule.builder("chat_room_msg")
                .actualTables(Arrays.asList("chat_room_msg"))
                .dataSourceRule(dataSourceRule)
                .build();

        //具体分库分表策略，按什么规则来分
        ShardingRule shardingRule = ShardingRule.builder()
                .dataSourceRule(dataSourceRule)
                .tableRules(Arrays.asList(orderTableRule))
                .databaseShardingStrategy(new DatabaseShardingStrategy("COURSE_ID", new DatabaseShardingAlgorithm()))
                .tableShardingStrategy(new TableShardingStrategy("ID", new TableShardingAlgorithm())).build();

        DataSource dataSource = ShardingDataSourceFactory.createDataSource(shardingRule);

        return dataSource;
    }
}