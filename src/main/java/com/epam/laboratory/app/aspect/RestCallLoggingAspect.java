package com.epam.laboratory.app.aspect;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.Collection;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RestCallLoggingAspect {

    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(restCallLogging)")
    public void restCallLoggingPointcut(RestCallLogging restCallLogging) {
    }

    @Around(
            value = "restCallLoggingPointcut(restCallLogging)",
            argNames = "joinPoint, restCallLogging"
    )
    public Object logRestCall(ProceedingJoinPoint joinPoint, RestCallLogging restCallLogging) throws Throwable {
        var request = getCurrentRequest();

        if (request != null) {
            logRequestInfo(request, joinPoint);
        }

        Object result;
        try {
            result = joinPoint.proceed();
            logResponseInfo(result);
        } catch (Throwable t) {
            log.error("REST call failed with exception: {}", t.getMessage());
            throw t;
        }

        return result;
    }

    private void logRequestInfo(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        var endpoint = request.getRequestURI();
        var httpMethod = request.getMethod();
        var args = joinPoint.getArgs();

        var requestInfo = new StringBuilder();
        requestInfo.append("REST Call - Endpoint: %s, HTTP Method: %s".formatted(endpoint, httpMethod));

        if (args != null && args.length > 0) {
            try {
                var argsToLog = args.length == 1 ? args[0] : args;
                argsToLog = maskSensitiveData(argsToLog);
                var requestBody = objectMapper.writeValueAsString(argsToLog);
                requestInfo.append(", Request Body: %s".formatted(requestBody));
            } catch (Exception e) {
                requestInfo.append(", Request Body: [Unable to serialize]");
            }
        }

        log.info(requestInfo.toString());
    }

    private void logResponseInfo(Object result) {
        int statusCode = 200;
        Object responseBody;

        if (result instanceof ResponseEntity<?> responseEntity) {
            statusCode = responseEntity.getStatusCode().value();
            responseBody = maskSensitiveData(responseEntity.getBody());
        } else {
            responseBody = maskSensitiveData(result);
        }

        try {
            String responseBodyStr = responseBody != null ? objectMapper.writeValueAsString(responseBody) : "null";
            log.info("REST Call Response - Status Code: {}, Response Body: {}", statusCode, responseBodyStr);
        } catch (Exception e) {
            log.info("REST Call Response - Status Code: {}, Response Body: [Unable to serialize]", statusCode);
        }
    }

    private HttpServletRequest getCurrentRequest() {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private Object maskSensitiveData(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            if (obj.getClass().isArray()) {
                Object[] array = (Object[]) obj;
                for (int i = 0; i < array.length; i++) {
                    array[i] = maskSensitiveData(array[i]);
                }
                return array;
            } else if (obj instanceof Collection<?> collection) {
                return collection.stream()
                        .map(this::maskSensitiveData)
                        .toList();
            } else {
                ObjectNode jsonNode = objectMapper.valueToTree(obj);
                Field[] fields = obj.getClass().getDeclaredFields();

                for (var field : fields) {
                    if (field.getType().isArray()
                            || Collection.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        var fieldValue = field.get(obj);
                        if (fieldValue != null) {
                            var maskedValue = maskSensitiveData(fieldValue);
                            jsonNode.putPOJO(field.getName(), maskedValue);
                        }
                        field.setAccessible(false);
                    } else if (field.isAnnotationPresent(Sensitive.class)) {
                        field.setAccessible(true);
                        jsonNode.put(field.getName(), "*".repeat(field.get(obj).toString().length()));
                        field.setAccessible(false);
                    }
                }
                return jsonNode;
            }
        } catch (Exception e) {
            return obj;
        }
    }

}

