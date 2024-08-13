package com.audition.common.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;

public class AuditionLoggerTest {

    @Mock
    private Logger mockLogger;

    private AuditionLogger loggingClass;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loggingClass = new AuditionLogger();
    }

    @Test
    void testInfoWithString() {
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        loggingClass.info(mockLogger, "Test message");
        verify(mockLogger).info("Test message");
    }

    @Test
    void testInfoWithStringObject() {
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        loggingClass.info(mockLogger, "Test message", new Object());
        verify(mockLogger).info(eq("Test message"), any(Object.class));
    }

    @Test
    void testInfoWithStringDisabled() {
        when(mockLogger.isInfoEnabled()).thenReturn(false);
        loggingClass.info(mockLogger, "Test message");
        verify(mockLogger, never()).info("Test message");
    }

    @Test
    void testDebug() {
        when(mockLogger.isDebugEnabled()).thenReturn(true);
        loggingClass.debug(mockLogger, "Test debug message");
        verify(mockLogger).debug("Test debug message");
    }

    @Test
    void testDebugDisabled() {
        when(mockLogger.isDebugEnabled()).thenReturn(false);
        loggingClass.debug(mockLogger, "Test debug message");
        verify(mockLogger, never()).debug("Test debug message");
    }

    @Test
    void testWarn() {
        when(mockLogger.isWarnEnabled()).thenReturn(true);
        loggingClass.warn(mockLogger, "Test warning message");
        verify(mockLogger).warn("Test warning message");
    }

    @Test
    void testWarnDisabled() {
        when(mockLogger.isWarnEnabled()).thenReturn(false);
        loggingClass.warn(mockLogger, "Test warning message");
        verify(mockLogger, never()).warn("Test warning message");
    }

    @Test
    void testError() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        loggingClass.error(mockLogger, "Test error message");
        verify(mockLogger).error("Test error message");
    }

    @Test
    void testErrorDisabled() {
        when(mockLogger.isErrorEnabled()).thenReturn(false);
        loggingClass.error(mockLogger, "Test error message");
        verify(mockLogger, never()).error("Test error message");
    }

    @Test
    void testLogErrorWithException() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        Exception ex = new Exception("Test exception");
        loggingClass.logErrorWithException(mockLogger, "Test error message", ex);
        verify(mockLogger).error(eq("Test error message"), eq(ex));
    }

    @Test
    void testLogErrorWithExceptionDisabled() {
        when(mockLogger.isErrorEnabled()).thenReturn(false);
        loggingClass.logErrorWithException(mockLogger, "Test error message", new Exception("Test exception"));
        verify(mockLogger, never()).error(any(String.class), any(Exception.class));
    }

    @Test
    void testLogStandardProblemDetail() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        ProblemDetail problemDetail = ProblemDetail.forStatus(500);
        problemDetail.setTitle("Error Title");
        problemDetail.setDetail("Error Detail");

        loggingClass.logStandardProblemDetail(mockLogger, problemDetail, new Exception("Test exception"));
        String expectedMessage = """
            Problem Detail:
              Status: 500
              Title: Error Title
              Detail: Error Detail
            """;
        verify(mockLogger).error(eq(expectedMessage), any(Exception.class));
    }

    @Test
    void testLogHttpStatusCodeError() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        loggingClass.logHttpStatusCodeError(mockLogger, "Test message", 404);
        String expectedMessage = """
            Basic Error Message:
              Status: 404
              Title: Test message
            """;
        verify(mockLogger).error("{}\n", expectedMessage);
    }

    @Test
    void testCreateStandardProblemDetailMessage() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(500);
        problemDetail.setTitle("Error Title");
        problemDetail.setDetail("Error Detail");

        String message = loggingClass.createStandardProblemDetailMessage(problemDetail);
        String expectedMessage = """
            Problem Detail:
              Status: 500
              Title: Error Title
              Detail: Error Detail
            """;
        assertEquals(expectedMessage, message);
    }

    @Test
    void testCreateBasicErrorResponseMessage() {
        String message = loggingClass.createBasicErrorResponseMessage(404, "Not Found");
        String expectedMessage = """
            Basic Error Message:
              Status: 404
              Title: Not Found
            """;
        assertEquals(expectedMessage, message);
    }
}
