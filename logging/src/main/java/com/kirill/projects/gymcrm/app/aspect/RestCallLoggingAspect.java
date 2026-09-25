package com.kirill.projects.gymcrm.app.aspect;

import com.kirill.projects.gymcrm.app.aspect.annotation.RestCallLogging;
import com.kirill.projects.gymcrm.app.exception.ApplicationException;
import com.kirill.projects.gymcrm.app.util.RequestIdHolder;
import com.kirill.projects.gymcrm.app.util.SensitiveDataMasker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RestCallLoggingAspect {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    private final JsonMapper objectMapper;
    protected final SensitiveDataMasker sensitiveDataMasker;

    @Pointcut("@annotation(restCallLogging)")
    public void restCallLoggingPointcut(RestCallLogging restCallLogging) {
    }

    @Before(
            value = "restCallLoggingPointcut(restCallLogging)",
            argNames = "joinPoint, restCallLogging"
    )
    public void logRestCallRequest(JoinPoint joinPoint, RestCallLogging restCallLogging) {
        getCurrentRequest().ifPresent(request ->
                logRequestInfo(joinPoint, request, restCallLogging.value()));
    }

    @AfterReturning(
            value = "restCallLoggingPointcut(restCallLogging)",
            returning = "result",
            argNames = "joinPoint, restCallLogging, result"
    )
    public void logRestCallResponse(JoinPoint joinPoint, RestCallLogging restCallLogging, Object result) {
        getCurrentResponse().ifPresent(response ->
                logResponseInfo(joinPoint, response, result, restCallLogging.value()));
    }

    @AfterThrowing(
            value = "restCallLoggingPointcut(restCallLogging)",
            throwing = "exception",
            argNames = "joinPoint, restCallLogging, exception"
    )
    public void logRestCallException(JoinPoint joinPoint, RestCallLogging restCallLogging, Throwable exception) {
        var logger = getLogger(joinPoint);
        var logMessage = "REST call resulted in exception: %s".formatted(exception.getMessage());
        logError(logger, logMessage, exception);
    }

    private void logRequestInfo(JoinPoint joinPoint, HttpServletRequest request, Level level) {
        var logger = getLogger(joinPoint);
        var endpoint = request.getRequestURI();
        var httpMethod = request.getMethod();
        var args = getRequestArgs(joinPoint);
        var requestId = request.getHeader(REQUEST_ID_HEADER) != null ? request.getHeader(REQUEST_ID_HEADER) : "N/A";

        var requestInfo = new StringBuilder("REST Call %s - Endpoint: %s, HTTP Method: %s".formatted(requestId, endpoint, httpMethod));
        if (args != null && args.length > 0) {
            try {
                var argsToLog = args.length == 1 ? args[0] : args;
                var maskedArgsToLog = sensitiveDataMasker.maskSensitiveData(argsToLog);
                var requestBody = objectMapper.writeValueAsString(maskedArgsToLog);
                requestInfo.append(", Request Body: %s".formatted(requestBody));
            } catch (Exception e) {
                requestInfo.append(", Request Body: [Unable to serialize] - %s".formatted(e.getMessage()));
            }
        }
        logByLevel(logger, level, requestInfo.toString());
    }

    private void logResponseInfo(JoinPoint joinPoint, HttpServletResponse response, Object result, Level level) {
        var logger = getLogger(joinPoint);
        var statusCode = response.getStatus();
        var responseBody = result instanceof  ResponseEntity<?> responseEntity
                ? sensitiveDataMasker.maskSensitiveData(responseEntity.getBody())
                : sensitiveDataMasker.maskSensitiveData(result);
        var requestId = RequestIdHolder.getRequestId() != null ? RequestIdHolder.getRequestId() : "N/A";
        RequestIdHolder.clearRequestId();
        try {
            var responseBodyStr = responseBody != null ? objectMapper.writeValueAsString(responseBody) : "null";
            var logMessage = "REST Call %s Response - Status Code: %d, Response Body: %s".formatted(requestId, statusCode, responseBodyStr);
            logByLevel(logger, level, logMessage);
        } catch (Exception e) {
            var logMessage = "REST Call %s Response - Status Code: %d, Response Body: [Unable to serialize] - %s".formatted(requestId, statusCode, e.getMessage());
            logByLevel(logger, level, logMessage);
        }
    }

    private Logger getLogger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    }

    private void logByLevel(Logger logger, Level level, String message) {
        logger.makeLoggingEventBuilder(level).log(message);
    }

    private void logError(Logger logger, String message, Throwable t) {
        if (t instanceof ApplicationException) {
            logger.warn(message);
        } else {
            logger.error(message, t);
        }
    }

    private Optional<HttpServletRequest> getCurrentRequest() {
        return Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes::getRequest);
    }

    private Optional<HttpServletResponse> getCurrentResponse() {
        return Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes::getResponse);
    }

    private static Object[] getRequestArgs(JoinPoint joinPoint) {
        return joinPoint.getArgs();
    }

}
