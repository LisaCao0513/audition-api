package com.audition.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceContextFilterConfig {

    @Bean
    public FilterRegistrationBean<TraceContextFilter> openTelemetryFilterRegistration() {
        FilterRegistrationBean<TraceContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceContextFilter());
        registration.addUrlPatterns("/*"); // Apply to all URLs or specific patterns
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }
}

