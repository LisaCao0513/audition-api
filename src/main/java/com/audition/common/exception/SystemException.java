package com.audition.common.exception;

import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class SystemException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5876728854007114881L;

    public static final String DEFAULT_TITLE = "API Error Occurred";
    private HttpStatusCode statusCode;
    private String title;
    private String detail;

    public SystemException() {
        super();
    }

    public SystemException(final String message) {
        super(message);
        this.title = DEFAULT_TITLE;
    }

    public SystemException(final String message, final HttpStatusCode errorCode) {
        super(message);
        this.title = DEFAULT_TITLE;
        this.statusCode = errorCode;
    }

    public SystemException(final String message, final Throwable exception) {
        super(message, exception);
        this.title = DEFAULT_TITLE;
    }

    public SystemException(final String detail, final String title, final HttpStatusCode errorCode) {
        super(detail);
        this.statusCode = errorCode;
        this.title = title;
        this.detail = detail;
    }

    public SystemException(final String detail, final String title, final Throwable exception) {
        super(detail, exception);
        this.title = title;
        this.statusCode = HttpStatus.valueOf(500);
        this.detail = detail;
    }

    public SystemException(final String detail, final HttpStatusCode errorCode, final Throwable exception) {
        super(detail, exception);
        this.statusCode = errorCode;
        this.title = DEFAULT_TITLE;
        this.detail = detail;
    }

    public SystemException(final String detail, final String title, final HttpStatusCode errorCode,
        final Throwable exception) {
        super(detail, exception);
        this.statusCode = errorCode;
        this.title = title;
        this.detail = detail;
    }
}
