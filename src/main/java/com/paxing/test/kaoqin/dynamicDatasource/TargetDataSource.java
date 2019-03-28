package com.paxing.test.kaoqin.dynamicDatasource;

import java.lang.annotation.*;

/**
 * 数据源注解
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/28
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String name() default "Master";
}
