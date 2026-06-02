package com.epam.laboratory.app.exception;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(exception = DtoValidationException.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @RestCallLogging
    protected ResponseEntity<Object> handleDtoValidationException(DtoValidationException e, WebRequest request) {
        var problemDetails = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return createResponseEntity(problemDetails, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(exception = AuthenticationException.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @RestCallLogging
    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException e, WebRequest request) {
        var problemDetails = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"Access to the protected resource\", charset=\"UTF-8\"");
        return createResponseEntity(problemDetails, httpHeaders, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(exception = IllegalArgumentException.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @RestCallLogging
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        var problemDetails = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        return createResponseEntity(problemDetails, null, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(exception = NoSuchEntityException.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @RestCallLogging
    protected ResponseEntity<Object> handleNoSuchEntityException(NoSuchEntityException e, WebRequest request) {
        var problemDetails = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        return createResponseEntity(problemDetails, null, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(exception = Exception.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @RestCallLogging
    protected ResponseEntity<Object> handleUnexpectedException(Exception e, WebRequest request) {
        var problemDetails = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        return createResponseEntity(problemDetails, null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
