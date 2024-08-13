package com.audition.configuration;

import io.opentelemetry.api.trace.Span;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class ResponseHeaderInjector {

    public void injectTraceContext(HttpServletResponse response) {
        Span currentSpan = Span.current();
        if (currentSpan != null && currentSpan.getSpanContext().isValid()) {
            String traceId = currentSpan.getSpanContext().getTraceId();
            String spanId = currentSpan.getSpanContext().getSpanId();

            response.setHeader("X-Trace-Id", traceId);
            response.setHeader("X-Span-Id", spanId);
        }
    }

}
