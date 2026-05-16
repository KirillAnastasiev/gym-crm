package com.epam.laboratory.app.aspect;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.exception.ApplicationException;
import com.epam.laboratory.app.util.SensitiveDataMasker;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
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

@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RestCallLoggingAspect {

    private final ObjectMapper objectMapper;
    protected final SensitiveDataMasker sensitiveDataMasker;

    @Pointcut("@annotation(restCallLogging)")
    public void restCallLoggingPointcut(RestCallLogging restCallLogging) {
    }

    @Before(
            value = "restCallLoggingPointcut(restCallLogging)",
            argNames = "joinPoint, restCallLogging"
    )
    public void logRestCallRequest(JoinPoint joinPoint, RestCallLogging restCallLogging) {
        var request = getCurrentRequest();
        if (request != null) {
            logRequestInfo(joinPoint, request, restCallLogging.value());
        }
    }

    @AfterReturning(
            value = "restCallLoggingPointcut(restCallLogging)",
            returning = "result",
            argNames = "joinPoint, restCallLogging, result"
    )
    public void logRestCallResponse(JoinPoint joinPoint, RestCallLogging restCallLogging, Object result) {
        logResponseInfo(joinPoint, result, restCallLogging.value());
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
        var endpoint = getRequestURI(request);
        var httpMethod = getRequestMethod(request);
        var args = getRequestArgs(joinPoint);

        var requestInfo = new StringBuilder("REST Call - Endpoint: %s, HTTP Method: %s".formatted(endpoint, httpMethod));
        if (args != null && args.length > 0) {
            try {
                var argsToLog = sensitiveDataMasker.maskSensitiveData(args);
                var requestBody = objectMapper.writeValueAsString(argsToLog);
                requestInfo.append(", Request Body: %s".formatted(requestBody));
            } catch (Exception e) {
                requestInfo.append(", Request Body: [Unable to serialize] - %s".formatted(e.getMessage()));
            }
        }
        logByLevel(logger, level, requestInfo.toString());
    }

    private void logResponseInfo(JoinPoint joinPoint, Object result, Level level) {
        var logger = getLogger(joinPoint);
        int statusCode = 200;
        Object responseBody;

        if (result instanceof ResponseEntity<?> responseEntity) {
            statusCode = getStatusCode(responseEntity);
            responseBody = sensitiveDataMasker.maskSensitiveData(responseEntity.getBody());
        } else {
            responseBody = sensitiveDataMasker.maskSensitiveData(result);
        }

        try {
            String responseBodyStr = responseBody != null ? objectMapper.writeValueAsString(responseBody) : "null";
            var logMessage = "REST Call Response - Status Code: %d, Response Body: %s".formatted(statusCode, responseBodyStr);
            logByLevel(logger, level, logMessage);
        } catch (Exception e) {
            var logMessage = "REST Call Response - Status Code: %d, Response Body: [Unable to serialize] - %s".formatted(statusCode, e.getMessage());
            logByLevel(logger, level, logMessage);
        }
    }

    private Logger getLogger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    }

    private void logByLevel(Logger logger, Level level, String message) {
        logger.makeLoggingEventBuilder(level).log(message);
    }

    private void logError(Logger logger, String message, Throwable exception) {
        if (exception instanceof ApplicationException) {
            logger.warn(message, exception);
        } else {
            logger.error(message, exception);
        }
    }

    private HttpServletRequest getCurrentRequest() {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private static Object[] getRequestArgs(JoinPoint joinPoint) {
        return joinPoint.getArgs();
    }

    private static String getRequestMethod(HttpServletRequest request) {
        return request.getMethod();
    }

    private static String getRequestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    private static int getStatusCode(ResponseEntity<?> responseEntity) {
        return responseEntity.getStatusCode().value();
    }

}
