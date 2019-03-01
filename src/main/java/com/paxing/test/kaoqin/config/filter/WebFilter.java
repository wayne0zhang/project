package com.paxing.test.kaoqin.config.filter;

import com.paxing.test.kaoqin.utils.IdCreator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 访问过滤器
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/28
 */
@Slf4j
public class WebFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long currentTime = System.currentTimeMillis();
        StringBuilder url = new StringBuilder();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            initRequest(request);
            url = url.append(request.getRequestURL().toString());
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            String string = url.toString();
            if (!string.contains("resource")) {
                log.info(String.format("time %s %s", url, System.currentTimeMillis() - currentTime));
            }
            MDC.clear();
        }

    }

    private void initRequest(HttpServletRequest request) {
        MDC.clear();
        // 全局不重复id，为了区分不同的访问
        long logId = IdCreator.get().nextId();

        // 还可以放入userId信息，可以在日志中取到，打印出来
        MDC.put("logID", logId + "");
    }

    @Override
    public void destroy() {

    }
}
