package com.paxing.test.kaoqin.config;

import com.alibaba.fastjson.JSON;
import com.paxing.test.kaoqin.exception.DefaultBizException;
import com.paxing.test.kaoqin.exception.UserVisiableException;
import org.apache.http.HttpStatus;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * 通用异常处理
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/21
 */
public class WebExceptionHandler extends AbstractHandlerExceptionResolver {

    private final static String COMMON_ERROR_MSG = "系统异常，请联系管理员";
    private final static int DEFAULT_CODE = -1;

    private static final String REQUEST_STR = "XMLHttpRequest";

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        logger.error(ex.getMessage(), ex);
        response.setStatus(HttpStatus.SC_OK);
        Exception parsedException = parseUserVisibleException(ex, request);

        if (REQUEST_STR.equalsIgnoreCase(request.getHeader("X-Request-With"))) {
            return this.handleJson(request, response, parsedException);
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.getMethodAnnotation(ResponseBody.class) != null
                    || handlerMethod.getMethod().getDeclaringClass().getAnnotation(RestController.class) != null) {
                return this.handleJson(request, response, parsedException);
            }
        }
        return this.handlePage(request, response, handler, parsedException);
    }

    private Integer getHttpStatus(Exception ex) {
        int httpCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            httpCode = responseStatus.value().value();
        }
        return httpCode;
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response) {
        this.handleException(request, response, COMMON_ERROR_MSG);
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, String message) {
        ExceptionResult result = new ExceptionResult(DEFAULT_CODE, message, System.currentTimeMillis(), request.getRequestURI());
        this.writeResponseMessage(response, result.toString());
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Integer code, String message) {
        ExceptionResult result = new ExceptionResult(code, message, System.currentTimeMillis(), request.getRequestURI());
        this.writeResponseMessage(response, result.toString());
    }


    /**
     * 输出json信息
     *
     * @param response
     * @param message
     */
    private void writeResponseMessage(HttpServletResponse response, String message) {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream())) {
            FileCopyUtils.copy(message.getBytes("UTF-8"), bufferedOutputStream);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 返回默认错误页面
     * <p>
     * <notice>
     * 如果返回的modelAndView 中，有对象存在，会被解析成页面的方式，如果没有指定的viewName，则会触发【circular view path】错误
     * 如果需要返回json 字符，则直接在响应流中写入即可。
     * </notice>
     *
     * @param request
     * @param response
     * @param handler
     * @param e
     * @return
     */
    private ModelAndView handlePage(HttpServletRequest request, HttpServletResponse response, Object handler,
                                    Exception e) {
        ModelAndView modelAndView = new ModelAndView("/common/exception/500");
        if (e instanceof MaxUploadSizeExceededException) {
            modelAndView.addObject("message", "上传文件过大");
        } else if (e instanceof UserVisiableException) {
            UserVisiableException ex = (UserVisiableException) e;
            modelAndView.addObject("code", ex.getErrorCode());
            modelAndView.addObject("message", ex.getMessage());
        } else if (e instanceof NoHandlerFoundException) {
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            modelAndView.addObject("message", e.getMessage());
        } else {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            modelAndView.addObject("message", e.getMessage());
        }
        return modelAndView;
    }


    /**
     * 处理JSON请求的时候发生的错误，向response里写入标准的json错误
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    private ModelAndView handleJson(HttpServletRequest request, HttpServletResponse response, Exception e) {
        // 打印参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        logger.error(JSON.toJSON(parameterMap));

        if (e instanceof DefaultBizException) {
            handleException(request, response, DEFAULT_CODE, e.getMessage());
        } else {
            response.setStatus(getHttpStatus(e));
            handleException(request, response);
        }
        return new ModelAndView();
    }

    private Exception parseUserVisibleException(Exception ex, HttpServletRequest request) {
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    static class ExceptionResult implements Serializable {

        private static final long serialVersionUID = 6802829211786658991L;

        private int code;

        private String message;

        private long timestamp;

        private String path;

        public ExceptionResult() {
        }

        public ExceptionResult(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public ExceptionResult(int code, String message, long timestamp, String path) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
            this.path = path;
        }

        @Override
        public String toString() {
            return "ExceptionResult{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", timestamp=" + timestamp +
                    ", path='" + path + '\'' +
                    '}';
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getPath() {
            return path;
        }
    }
}
