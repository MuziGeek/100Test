package com.muzi.part17.trace;

import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <b>description</b>： Java高并发、微服务、性能优化实战案例100讲，视频号：程序员路人，源码 & 文档 & 技术支持，请加个人微信号：itsoku <br>
 * <b>time</b>：2024/3/24 11:17 <br>
 * <b>author</b>：ready likun_557@163.com
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(urlPatterns = "/**", filterName = "TraceFilter")
public class TraceFilter extends OncePerRequestFilter {
    public static Logger logger = LoggerFactory.getLogger(TraceFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceID = IdUtil.fastSimpleUUID();
        TraceUtils.setTraceId(traceID);
        long st = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long et = System.currentTimeMillis();
            logger.info("请求地址:{},耗时(ms):{}", request.getRequestURL().toString(), (et - st));
            TraceUtils.removeTraceId();
        }
    }
}
