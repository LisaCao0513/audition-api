package com.audition.configuration;

import io.micrometer.common.lang.NonNullApi;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@NonNullApi
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(
        HttpRequest request,
        byte[] body,
        ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        // Execute the request
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        logger.info("Request: {} {}", request.getMethod(), request.getURI());
        logger.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        logger.info("Response Status Code: {}", response.getStatusCode());
        InputStream responseBody = response.getBody();
        String responseBodyString = new String(responseBody.readAllBytes(), StandardCharsets.UTF_8);
        logger.info("Response Body: {}", responseBodyString);
    }
}
