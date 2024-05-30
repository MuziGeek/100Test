package com.muzi.part17.trace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
public class TraceConfiguration {
    @Bean
    public TraceFilter traceFilter() {
        return new TraceFilter();
    }

    @Bean
    public ResultTraceIdAspect fillRequestIdAspect() {
        return new ResultTraceIdAspect();
    }
}
