package com.paxing.test.kaoqin.config;

import com.paxing.test.kaoqin.config.filter.WebFilter;
import com.paxing.test.kaoqin.config.interceptor.RequestLoggerHandler;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
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
    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggerHandler()).excludePathPatterns("/api/**").addPathPatterns("/**");
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

    /**
     * 增加过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean webFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setName("webFilter");
        registrationBean.setFilter(new WebFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public RequestLoggerHandler requestLoggerHandler() {
        return new RequestLoggerHandler();
    }
}
