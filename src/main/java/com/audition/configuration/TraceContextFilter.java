package com.audition.configuration;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class TraceContextFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";

    private Logger logger;

    @Override
    public void init(FilterConfig filterConfig) {
        // Initialize a logger
        logger = LoggerFactory.getLogger(TraceContextFilter.class);
        logger.info("TraceContextFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpResponse) {
            Span currentSpan = Span.current();
            SpanContext spanContext = currentSpan.getSpanContext();
            // Add trace and span IDs to the response headers
            httpResponse.setHeader(TRACE_ID_HEADER, spanContext.getTraceId());
            httpResponse.setHeader(SPAN_ID_HEADER, spanContext.getSpanId());
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        logger.info("TraceContextFilter destroyed.");
    }
}

