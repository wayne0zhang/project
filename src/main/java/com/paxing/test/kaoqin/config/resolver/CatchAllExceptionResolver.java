package com.paxing.test.kaoqin.config.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 捕捉所有异常，并转化为标准输出
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/20
 */
public class CatchAllExceptionResolver extends AbstractResultExceptionResolver {

    /**
     * 我们支持的类型
     */
    private final Set<DispatcherType> supportDispatchTypes =
            new HashSet<>(Arrays.asList(DispatcherType.REQUEST, DispatcherType.ASYNC));

    /**
     * Spring MVC异常类对应的http 状态码
     */
    Map<String, Integer> exceptionHttpCodeMap = new HashMap<>();
    private final boolean logErrorTrace;

    /**
     * 优先级要比spring自己配置的要高
     *
     * @return
     * @since: 1.0.10
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 20000000;
    }

    /**
     * 设置异常和http状态码对应关系
     *
     * @author huisman
     * @version v1
     */
    private void loadingExceptionCodeMapping() {
        Properties properties = new Properties();
        // 绝对路径开始
        try (InputStream in = this.getClass()
                .getResourceAsStream("/static/spring-mvc-exception-httpstatus-mapping.properties")) {
            properties.load(in);
            Enumeration<Object> keys = properties.keys();
            while (keys.hasMoreElements()) {
                String ex = (String) keys.nextElement();
                this.exceptionHttpCodeMap.put(ex,
                        Integer.parseInt(String.valueOf(properties.getProperty(ex))));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CatchAllExceptionResolver(boolean logErrorTrace) {
        loadingExceptionCodeMapping();
        this.logErrorTrace = logErrorTrace;
        logger.warn("=====>> 所有请求（仅限使用@ResponseBody标注的方法）异常将转换为标准响应json");
    }

    @Override
    protected boolean shouldResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex == null) {
            return false;
        }
        // dispatch
        if (!supportDispatchTypes.contains(request.getDispatcherType())) {
            return false;
        }
//        if (RestResponseMessageProviders.support(ex)) {
//            return false;
//        }

        if (handler == null) {
            // 没找到handler时。我们也拦截，比如请求方法不正确。。等等。
            return true;
        }
        // 处理HandlerMethod，仅限Rest接口
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            MethodParameter returnType = handlerMethod.getReturnType();
            if (AnnotationUtils.findAnnotation(returnType.getContainingClass(),
                    ResponseBody.class) == null
                    && returnType.getMethodAnnotation(ResponseBody.class) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        // 如果是SpringMVC内部异常，则没必要打印堆栈
        if (this.logErrorTrace && isSpringInternalError(ex, request)) {
            this.logger.error(formatLogMessage(false, ex, request));
        } else {
            this.logger.error(formatLogMessage(true, ex, request));
        }
    }

    private boolean isSpringInternalError(Exception ex, HttpServletRequest request) {
        return ex != null && this.exceptionHttpCodeMap.containsKey(ex.getClass().getName());
    }

    @Override
    protected RestResponseMessage resolveResponseMessage(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        // 首先查找是否是SpringMVC抛出的异常
        Integer mvcExceptionHttpCode = this.exceptionHttpCodeMap.get(ex.getClass().getName());
        String responseReason = null;
        if (mvcExceptionHttpCode != null) {
            statusCode = mvcExceptionHttpCode;
        } else {
            ResponseStatus responseStatus =
                    AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
            if (responseStatus != null) {
                statusCode = responseStatus.value().value();
                responseReason = responseStatus.reason();
            }
        }
        if (!StringUtils.hasText(responseReason)) {
            responseReason = ex.getMessage();
        }
        // 如果异常没有抛出message，则默认返回异常类
        if (!StringUtils.hasText(responseReason)) {
            responseReason = ex.getClass().getName();
        }
        return new RestResponseMessage(-1, responseReason, statusCode);
    }
}
