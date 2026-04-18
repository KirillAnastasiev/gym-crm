package com.epam.laboratory.app.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Before("@annotation(logging)")
    public void logMethodEntry(JoinPoint joinPoint, Logging logging) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        String logMessage = String.format("Entering method: %s.%s with arguments: %s",
                className, methodName, Arrays.toString(args));

        logByLevel(logging.value(), logMessage);
    }

    @AfterReturning(pointcut = "@annotation(logging)", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Logging logging, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        String logMessage = String.format("Exiting method: %s.%s with result: %s", className, methodName, result);

        logByLevel(logging.value(), logMessage);
    }

    @AfterThrowing(pointcut = "@annotation(logging)", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Logging logging, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        String logMessage = String.format("Exception in method: %s.%s with message: %s",
                className, methodName, exception.getMessage());

        log.error(logMessage, exception);
    }

    private void logByLevel(Level level, String message) {
        switch (level) {
            case DEBUG -> log.debug(message);
            case INFO -> log.info(message);
            case WARN -> log.warn(message);
            case ERROR -> log.error(message);
            default -> log.trace(message);
        }
    }
}


