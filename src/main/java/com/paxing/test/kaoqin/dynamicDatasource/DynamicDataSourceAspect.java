package com.paxing.test.kaoqin.dynamicDatasource;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/28
 */
@Slf4j
@Aspect
@Order(-1)
@Component
public class DynamicDataSourceAspect {

    /**
     * 在方法执行之前执行 @annotation(ds),会拦截有 TargetDataSource ds这个注解的方法
     *
     * @param point
     * @param ds
     * @throws Throwable
     */
    @Before("@annotation(ds)")
    public void changeDataSource(JoinPoint point, TargetDataSource ds) {
        String name = ds.name();
        DynamicDataSourceContextHolder.setDataSourceType(name);
    }

    /**
     * 在方法执行之后执行 @annotation(ds), 会拦截有TargetDataSource ds这个注解的方法
     *
     * @param point
     * @param ds
     */
    @After("@annotation(ds)")
    public void restoreDataSource(JoinPoint point, TargetDataSource ds) {
        log.debug("------- Revert DataSource : {} > {}", ds.name(), point.getSignature());
        DynamicDataSourceContextHolder.clearDataSource();
    }

    /**
     * 在方法执行异常之后执行 @annotation(ds), 会拦截有TargetDataSource ds这个注解的方法
     *
     * @param point
     * @param dataSource
     */
    @AfterThrowing("@annotation(dataSource)")
    public void clearDataSource(JoinPoint point, TargetDataSource dataSource) {
        log.debug("------- Revert DataSource : {} > {}", dataSource.name(), point.getSignature());
        DynamicDataSourceContextHolder.clearDataSource();
    }
}
