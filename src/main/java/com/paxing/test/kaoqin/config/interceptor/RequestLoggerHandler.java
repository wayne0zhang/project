package com.paxing.test.kaoqin.config.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * url请求信息记录
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/28
 */
@Slf4j
public class RequestLoggerHandler extends HandlerInterceptorAdapter {
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        startTime.set(System.currentTimeMillis());
        log.info("[http]preHandle url: {}, inParams: {}, ucIdHeader:{}", request.getRequestURL(),
                toJSONString(request.getParameterMap()), request.getHeader("ucID"));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        log.info("[http]postHandle url: {}, cause [{}] ms", request.getRequestURL(),
                System.currentTimeMillis() - startTime.get());
    }

    private String toJSONString(Object object) {
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {
            log.warn("request请求参数序列化{}异常......");
            e.printStackTrace();
        }
        return null;
    }
}
