package com.mentoree.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TimeElapseAop {

    private Logger logger = LoggerFactory.getLogger("TimeElapseTrace");

    @Around("execution(* com.mentoree.*.*(..))")
    public void timeElapseLog(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        long elapseTime = (endTime - startTime) / 1000;

        if(elapseTime > 5) {
            logger.warn("Bottleneck method - {}", joinPoint.getSignature());
            logger.warn("ElapsedTime - {}", endTime - startTime);
        }
    }

}
