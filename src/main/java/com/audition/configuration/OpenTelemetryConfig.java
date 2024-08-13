package com.audition.configuration;

import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public Tracer tracer() {
        // Create an OTLP gRPC Span Exporter
        OtlpGrpcSpanExporter otlpExporter = OtlpGrpcSpanExporter.builder()
            .setEndpoint("http://localhost:4317") // Replace with your OTLP endpoint
            .build();

        // Create a Span Processor
        SimpleSpanProcessor spanProcessor = (SimpleSpanProcessor) SimpleSpanProcessor.create(otlpExporter);

        // Create a Tracer Provider
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(spanProcessor)
            .build();

        // Build and register the OpenTelemetry SDK globally
        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .buildAndRegisterGlobal();

        // Return the Tracer instance
        return openTelemetrySdk.getTracer("my-instrumentation-library");
    }
}
