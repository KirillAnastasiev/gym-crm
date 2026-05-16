package com.epam.laboratory.app.aspect;

import com.epam.laboratory.app.aspect.annotation.ValidateArguments;
import com.epam.laboratory.app.util.DtoValidator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Collection;

@Aspect
@Component
@Order(1)
public class RestRequestArgumentsValidatingAspect {

    @Pointcut("@annotation(validateArguments)")
    public void validationPointcut(ValidateArguments validateArguments) {
    }

    @Before(
            value = "validationPointcut(validateArguments)",
            argNames = "joinPoint, validateArguments"
    )
    public void validateRestRequestArguments(JoinPoint joinPoint, ValidateArguments validateArguments) {
        var parameters = getRequestParameters(joinPoint);
        var args = getMethodArgs(joinPoint);

        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                checkPathVariable(parameters, i, args);
                checkRequestBody(parameters, i, args);
            }
        }
    }

    private static void checkPathVariable(Parameter[] parameters, int index, Object[] args) {
        if (isAnnotatedWith(parameters[index], PathVariable.class) && isBlancString(args[index])) {
            throw new IllegalArgumentException("Path variable '%s' must not be blank".formatted(parameters[index].getName()));

        }
    }

    private static void checkRequestBody(Parameter[] parameters, int index, Object[] args) {
        if (isAnnotatedWith(parameters[index], RequestBody.class)) {
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

    private static Parameter[] getRequestParameters(JoinPoint joinPoint) {
        var signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getParameters();
    }

    private static Object[] getMethodArgs(JoinPoint joinPoint) {
        return joinPoint.getArgs();
    }

    private static boolean isAnnotatedWith(Parameter parameters, Class<? extends Annotation> annotationClass) {
        return parameters.isAnnotationPresent(annotationClass);
    }

    private static boolean isBlancString(Object args) {
        return args instanceof String str && str.isBlank();
    }
}