package com.kirill.projects.gymcrm.app.exception;

import com.kirill.projects.gymcrm.app.aspect.annotation.RestCallLogging;
import com.kirill.projects.gymcrm.app.util.RequestIdHolder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @ExceptionHandler(exception = DtoValidationException.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @RestCallLogging
    protected ResponseEntity<Object> handleDtoValidationException(DtoValidationException e, WebRequest request) {
        return getResponseEntity(e, request, null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(exception = AuthenticationException.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @RestCallLogging
    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException e, WebRequest request) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"Access to the protected resource\", charset=\"UTF-8\"");
        return getResponseEntity(e, request, httpHeaders, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(exception = IllegalArgumentException.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @RestCallLogging
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        return getResponseEntity(e, request, null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(exception = NoSuchEntityException.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @RestCallLogging
    protected ResponseEntity<Object> handleNoSuchEntityException(NoSuchEntityException e, WebRequest request) {
        return getResponseEntity(e, request, null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(exception = Exception.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @RestCallLogging
    protected ResponseEntity<Object> handleUnexpectedException(Exception e, WebRequest request) {
        return getResponseEntity(e, request, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> getResponseEntity(Exception exception,
                                                     WebRequest request,
                                                     HttpHeaders headers,
                                                     HttpStatus status) {
        headers = headers != null ? headers : new HttpHeaders();
        var requestId = getRequestId(request);
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        headers.add(REQUEST_ID_HEADER, requestId);
        var problemDetails = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        return handleExceptionInternal(exception, problemDetails, headers, status, request);
    }

    private static String getRequestId(WebRequest request) {
        return request.getHeader(REQUEST_ID_HEADER) != null
                ? request.getHeader(REQUEST_ID_HEADER)
                : RequestIdHolder.getRequestId() != null
                    ? RequestIdHolder.getRequestId()
                    : "N/A";
    }

}
