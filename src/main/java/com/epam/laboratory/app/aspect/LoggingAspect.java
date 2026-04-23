package com.epam.laboratory.app.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("@annotation(logging)")
    public void executeLoggingAdvice(Logging logging) {}

    @Pointcut("execution(* *.*(..)) && !execution(* *.*())")
    public void executeMethodWithArgs() {}

    @Pointcut("execution(* *.*())")
    public void executeMethodWithoutArgs() {}

    @Pointcut("execution(void *.*(..))")
    public void executeVoidMethod() {}

    @Pointcut("execution(* *.*(..)) && !execution(void *.*(..))")
    public void executeNotVoidMethod() {}

    @Before(value = "executeLoggingAdvice(logging) && executeMethodWithArgs()",
            argNames = "joinPoint, logging")
    public void logMethodEntryWithArguments(JoinPoint joinPoint, Logging logging) {
        String className = getClassName(joinPoint);
        String methodName = getMethodName(joinPoint);
        Object[] args = joinPoint.getArgs();
        String logMessage = String.format("Entering method: %s.%s with arguments: %s", className, methodName, Arrays.toString(args));
        logByLevel(logging.value(), logMessage);
    }

    @Before(value = "executeLoggingAdvice(logging) && executeMethodWithoutArgs()",
            argNames = "joinPoint, logging")
    public void logMethodEntryWithoutArguments(JoinPoint joinPoint, Logging logging) {
        String className = getClassName(joinPoint);
        String methodName = getMethodName(joinPoint);
        String logMessage = String.format("Entering method: %s.%s", className, methodName);
        logByLevel(logging.value(), logMessage);
    }

    @AfterReturning(pointcut = "executeLoggingAdvice(logging) && executeNotVoidMethod()",
                    argNames = "joinPoint, logging, result",
                    returning = "result")
    public void logMethodExitWithResult(JoinPoint joinPoint, Logging logging, Object result) {
        String className = getClassName(joinPoint);
        String methodName = getMethodName(joinPoint);
        String logMessage = String.format("Exiting method: %s.%s with result: %s", className, methodName, result);
        logByLevel(logging.value(), logMessage);
    }

    @AfterReturning(value = "executeLoggingAdvice(logging) && executeVoidMethod()",
                    argNames = "joinPoint, logging")
    public void logVoidMethodExit(JoinPoint joinPoint, Logging logging) {
        String className = getClassName(joinPoint);
        String methodName = getMethodName(joinPoint);
        String logMessage = String.format("Exiting method: %s.%s", className, methodName);
        logByLevel(logging.value(), logMessage);
    }

    @AfterThrowing(value = "executeLoggingAdvice(logging)",
                   argNames = "joinPoint, logging, exception",
                   throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Logging logging, Throwable exception) {
        String methodName = getMethodName(joinPoint);
        String className = getClassName(joinPoint);
        String logMessage = String.format("Exception in method: %s.%s with message: %s", className, methodName, exception.getMessage());
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

    private static String getClassName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName();
    }

    private static String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }
}


