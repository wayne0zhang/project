package com.paxing.test.kaoqin.config.resolver;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 将所有的异常转换为错误信息输出
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/20
 */
public abstract class AbstractResultExceptionResolver
        extends AbstractHandlerExceptionResolver {

    private List<HttpMessageConverter<?>> messageConverters = new ArrayList<>(new BuiltinRestHttpMessageConverters().get());

    private RemoteIpResolver remoteIpResolver = new RemoteIpResolver();

    public AbstractResultExceptionResolver() {
        super();
        this.messageConverters = new BuiltinRestHttpMessageConverters().get();
    }

    public AbstractResultExceptionResolver(
            List<HttpMessageConverter<?>> additionalMessageConverters) {
        super();
        this.messageConverters =
                new BuiltinRestHttpMessageConverters(additionalMessageConverters).get();
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        try {
            if (shouldResolveException(request, response, handler, ex)) {
                return writeResponseMessage(request, response, handler, ex);
            }
        } catch (Exception resolveEx) {
            logger.warn("error occured when handle  exception, context :"
                    + formatLogMessage(false, resolveEx, request), resolveEx);
        }
        return null;
    }

    private ModelAndView writeResponseMessage(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        RestResponseMessage restResponseMessage = resolveResponseMessage(request, response, handler, ex);
        response.setStatus(restResponseMessage.getHttpStatus());
        writeMessage(new ResultMessage(restResponseMessage.getCode(), restResponseMessage.getMessage()), request, response);
        return new ModelAndView();
    }

    private void writeMessage(ResultMessage resultMessage, HttpServletRequest request, HttpServletResponse response) throws IOException {
        resultMessage.setTimestamp(System.currentTimeMillis());
        resultMessage.setPath(request.getRequestURI());
        ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);
        Class<?> resultClass = resultMessage.getClass();

        MediaType applicationJson = MediaType.APPLICATION_JSON;
        for (HttpMessageConverter messageConverter : messageConverters) {
            if (messageConverter.canWrite(resultClass, applicationJson)) {
                messageConverter.write(resultMessage, applicationJson, servletServerHttpResponse);
                return;
            }
        }
    }

    @Override
    protected String buildLogMessage(Exception ex, HttpServletRequest request) {
        if (request == null) {
            return super.buildLogMessage(ex, request);
        }
        return formatLogMessage(false, ex, request);
    }

    final protected String formatLogMessage(boolean logExceptionClassAndMessage, Exception ex, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder(220);
        sb.append("method:" + request.getMethod())
                .append(",path:" + request.getRequestURI())
                .append(",query:" + request.getQueryString())
                .append(",remoteIp:" + this.remoteIpResolver.getClientIp(request));

        if (logExceptionClassAndMessage) {
            sb.append(",exception:" + ex.getClass().getName()).append("(").append(ex.getMessage()).append(")");
        }
        return sb.toString();
    }

    /**
     * 是否应该解析异常，如果可以解析，则子类必须实现
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return
     */
    protected abstract boolean shouldResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);

    /**
     * 解析我们应该返回的HttpStatus以及json body数据
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return
     */
    protected abstract RestResponseMessage resolveResponseMessage(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);

    /**
     * 响应消息
     * 指定HttpStatus响应码，另外生成body信息
     */
    protected static class ResponseMessage {
        /**
         * http响应码
         */
        private int httpStatusCode;

        /**
         * http响应body
         */
        private ResultMessage result;

        public ResponseMessage(int httpStatusCode, int resultCode, String message) {
            super();
            this.httpStatusCode = httpStatusCode;
            this.result = new ResultMessage(resultCode, message);
        }

        @Override
        public String toString() {
            return "ResponseMessage{" +
                    "httpStatusCode=" + httpStatusCode +
                    ", result=" + result +
                    '}';
        }

        public int getHttpStatusCode() {
            return httpStatusCode;
        }

        public ResultMessage getResult() {
            return result;
        }
    }

    static class RemoteIpResolver {
        private static final String[] OPTIONAL_IP_INDICATE_HEADERS =
                new String[]{"X-Real-IP", "X-Client-Id", "X-forwarded-for"};

        String getClientIp(HttpServletRequest request) {
            String ip = null;
            for (String header : OPTIONAL_IP_INDICATE_HEADERS) {
                ip = request.getHeader(header);
                if (ip == null || ip.trim().isEmpty() || "unknow".equalsIgnoreCase(ip)) {
                    continue;
                }
                break;
            }

            // 处理ip
            if (ip == null) {
                return request.getRemoteAddr();
            } else {
                int index = -1;
                if ((index = ip.indexOf(',')) > 0) {
                    // 如果经过节点转发，ip可能有多个，取第一个
                    ip = ip.substring(0, index);
                }
                return ip;
            }
        }
    }

    /**
     * 标准错误json body信息
     */
    static class ResultMessage implements Serializable {
        private static final long serialVersionUID = 1L;
        private int code;
        private String message;
        private String path;
        private long timestamp;

        public ResultMessage(int code, String message) {
            super();
            this.message = message;
            this.code = code;
        }

        @Override
        public String toString() {
            return "ResultMessage{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", path='" + path + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
