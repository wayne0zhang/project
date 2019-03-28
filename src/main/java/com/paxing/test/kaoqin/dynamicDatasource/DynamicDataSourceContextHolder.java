package com.paxing.test.kaoqin.dynamicDatasource;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态数据源上下文
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/28
 */
public class DynamicDataSourceContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static String getDataSourceType() {
        return contextHolder.get();
    }

    public static void clearDataSource() {
        contextHolder.remove();
    }

    public static void setDataSourceType(String dataSourceType) {
        contextHolder.set(dataSourceType);
    }

    public static List<String> dataSourceIds = new ArrayList<>();

    /**
     * 判定是否存在
     *
     * @param dataSourceId
     * @return
     */
    public static boolean containsDataSource(String dataSourceId) {
        return dataSourceIds.contains(dataSourceId);
    }
}
