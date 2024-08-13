package com.audition.web.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.audition.common.exception.SystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.HttpClientErrorException;

public class ExceptionControllerAdviceTest {

    @InjectMocks
    private ExceptionControllerAdvice exceptionControllerAdvice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleHttpClientException() {
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found");
        ProblemDetail result = exceptionControllerAdvice.handleHttpClientException(exception);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatus());
        assertEquals("404 Not Found", result.getDetail());
        assertEquals("API Error Occurred", result.getTitle());
    }

    @Test
    void testHandleMainException() {
        Exception exception = new Exception("General error");
        ProblemDetail result = exceptionControllerAdvice.handleMainException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
        assertEquals("General error", result.getDetail());
        assertEquals("API Error Occurred", result.getTitle());
    }

    @Test
    void testHandleSystemExceptionWithValidStatusCode() {
        SystemException exception = new SystemException("Custom error", HttpStatus.valueOf(400));
        ProblemDetail result = exceptionControllerAdvice.handleSystemException(exception);

        assertEquals(400, result.getStatus());
        assertEquals("Custom error", result.getDetail());
        assertEquals("API Error Occurred", result.getTitle());
    }

    @Test
    void testGetMessageFromExceptionWithMessage() {
        String message = exceptionControllerAdvice.getMessageFromException(new Exception("Test message"));
        assertEquals("Test message", message);
    }

    @Test
    void testGetMessageFromExceptionWithoutMessage() {
        String message = exceptionControllerAdvice.getMessageFromException(new Exception());
        assertEquals("API Error occurred. Please contact support or administrator.", message);
    }

    @Test
    void testGetHttpStatusCodeFromExceptionHttpClientErrorException() {
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        HttpStatusCode statusCode = exceptionControllerAdvice.getHttpStatusCodeFromException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, statusCode);
    }

    @Test
    void testGetHttpStatusCodeFromExceptionHttpRequestMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("POST");
        HttpStatusCode statusCode = exceptionControllerAdvice.getHttpStatusCodeFromException(exception);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, statusCode);
    }

    @Test
    void testGetHttpStatusCodeFromExceptionUnknown() {
        Exception exception = new Exception();
        HttpStatusCode statusCode = exceptionControllerAdvice.getHttpStatusCodeFromException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
    }

    @Test
    void testGetHttpStatusCodeFromSystemExceptionWithValidStatusCode() {
        SystemException exception = new SystemException("Custom error", HttpStatus.valueOf(200));
        HttpStatusCode statusCode = exceptionControllerAdvice.getHttpStatusCodeFromSystemException(exception);

        assertEquals(HttpStatusCode.valueOf(200), statusCode);
    }
}
