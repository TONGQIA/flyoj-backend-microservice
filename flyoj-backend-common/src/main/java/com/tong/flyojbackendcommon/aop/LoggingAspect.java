package com.tong.flyojbackendcommon.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controller() {}

    @Around("controller()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // continue on the intercepted method
        long elapsedTime = System.currentTimeMillis() - start;

        String methodName = joinPoint.getSignature().toShortString();
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] arguments = joinPoint.getArgs();

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < arguments.length; i++) {
            params.put(parameterNames[i], arguments[i]);
        }

        log.info("方法名: {}, 入参: {}, 出参: {}, 执行时间: {} ms", methodName, params, result, elapsedTime);

        return result;
    }

}
