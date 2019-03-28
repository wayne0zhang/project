package com.paxing.test.kaoqin.dynamicDatasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

/**
 * 动态数据源
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/28
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = DynamicDataSourceContextHolder.getDataSourceType();
        if (dataSourceName == null) {
            return "Master";
        }
        return dataSourceName;
    }
}
