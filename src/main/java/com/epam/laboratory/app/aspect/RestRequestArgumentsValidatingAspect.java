package com.epam.laboratory.app.aspect;

import com.epam.laboratory.app.aspect.annotation.ValidateArguments;
import com.epam.laboratory.app.util.DtoValidator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Parameter;
import java.util.Collection;

@Aspect
@Component
public class RestRequestArgumentsValidatingAspect {

    @Pointcut("@annotation(validateArguments)")
    public void executeValidationAdvice(ValidateArguments validateArguments) {
    }

    @Before(
            value = "executeValidationAdvice(validateArguments)",
            argNames = "joinPoint, validateArguments"
    )
    public void validateRestRequestArguments(JoinPoint joinPoint, ValidateArguments validateArguments) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                chackPathVariable(parameters, i, args);
                checkRequestBody(parameters, i, args);
            }
        }
    }

    private static void chackPathVariable(Parameter[] parameters, int index, Object[] args) {
        if (parameters[index].isAnnotationPresent(PathVariable.class)
                && args[index] instanceof String str && str.isBlank()) {
            throw new IllegalArgumentException("Path variable '" + parameters[index].getName() + "' must not be blank");

        }
    }

    private static void checkRequestBody(Parameter[] parameters, int index, Object[] args) {
        if (parameters[index].isAnnotationPresent(RequestBody.class)) {
            if (args[index].getClass().isRecord()) {
                DtoValidator.validate(args[index]);
            } else if (args[index] instanceof Collection<?> collection) {
                for (var element : collection) {
                    if (element.getClass().isRecord()) {
                        DtoValidator.validate(element);
                    }
                }
            }
        }
    }
}
