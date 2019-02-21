package com.paxing.test.kaoqin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * web mvc 配置，这里可以集中添加拦截器、过滤器、静态资源等
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary web mvc 配置
 * @since 2019/2/20
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }

    /**
     * 全局异常处理
     *
     * @param resolvers
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new WebExceptionHandler());
    }


}
