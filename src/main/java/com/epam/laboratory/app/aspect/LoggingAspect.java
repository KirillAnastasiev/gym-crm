package com.epam.laboratory.app.aspect;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoggingAspect {

    @Pointcut("@annotation(logging)")
    public void loggingPointcut(Logging logging) {}

    @Pointcut("execution(* *.*(..)) && !execution(* *.*())")
    public void executeMethodWithArgsPointcut() {}

    @Pointcut("execution(* *.*())")
    public void executeMethodWithoutArgsPointcut() {}

    @Pointcut("execution(void *.*(..))")
    public void executeVoidMethodPointcut() {}

    @Pointcut("execution(* *.*(..)) && !execution(void *.*(..))")
    public void executeNotVoidMethodPointcut() {}

    @Before(
            value = "loggingPointcut(logging) && executeMethodWithArgsPointcut()",
            argNames = "joinPoint, logging"
    )
    public void logMethodEntryWithArguments(JoinPoint joinPoint, Logging logging) {
        var logger = getLogger(joinPoint);
        var className = getClassName(joinPoint);
        var methodName = getMethodName(joinPoint);
        var args = joinPoint.getArgs();
        var logMessage = "Entering method: %s.%s with arguments: %s".formatted(className, methodName, Arrays.toString(args));
        logByLevel(logger,logging.value(), logMessage);
    }

    @Before(
            value = "loggingPointcut(logging) && executeMethodWithoutArgsPointcut()",
            argNames = "joinPoint, logging"
    )
    public void logMethodEntryWithoutArguments(JoinPoint joinPoint, Logging logging) {
        var logger = getLogger(joinPoint);
        var className = getClassName(joinPoint);
        var methodName = getMethodName(joinPoint);
        var logMessage = "Entering method: %s.%s".formatted(className, methodName);
        logByLevel(logger, logging.value(), logMessage);
    }

    @AfterReturning(
            pointcut = "loggingPointcut(logging) && executeNotVoidMethodPointcut()",
            argNames = "joinPoint, logging, result",
            returning = "result"
    )
    public void logMethodExitWithResult(JoinPoint joinPoint, Logging logging, Object result) {
        var logger = getLogger(joinPoint);
        var className = getClassName(joinPoint);
        var methodName = getMethodName(joinPoint);
        var logMessage = "Exiting method: %s.%s with result: %s".formatted(className, methodName, result);
        logByLevel(logger, logging.value(), logMessage);
    }

    @AfterReturning(
            value = "loggingPointcut(logging) && executeVoidMethodPointcut()",
            argNames = "joinPoint, logging"
    )
    public void logVoidMethodExit(JoinPoint joinPoint, Logging logging) {
        var logger = getLogger(joinPoint);
        var className = getClassName(joinPoint);
        var methodName = getMethodName(joinPoint);
        var logMessage = "Exiting method: %s.%s".formatted(className, methodName);
        logByLevel(logger, logging.value(), logMessage);
    }

    @AfterThrowing(
            value = "loggingPointcut(logging)",
            argNames = "joinPoint, logging, exception",
            throwing = "exception"
    )
    public void logMethodException(JoinPoint joinPoint, Logging logging, Throwable exception) {
        var logger = getLogger(joinPoint);
        var methodName = getMethodName(joinPoint);
        var className = getClassName(joinPoint);
        var logMessage = "Exception in method: %s.%s with message: %s".formatted(className, methodName, exception.getMessage());
        logError(logger, logMessage, exception);
    }

    private Logger getLogger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    }

    private void logByLevel(Logger logger, Level level, String message) {
        logger.makeLoggingEventBuilder(level).log(message);
    }

    private void logError(Logger logger, String message, Throwable throwable) {
        if (throwable instanceof ApplicationException) {
            logger.warn(message);
        } else {
            logger.error(message, throwable);
        }
    }

    private static String getClassName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName();
    }

    protected static String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }
}


