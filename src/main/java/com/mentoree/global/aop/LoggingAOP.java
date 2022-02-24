package com.mentoree.global.aop;

import com.mentoree.global.logging.LogTrace;
import com.mentoree.global.logging.TraceStatus;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Aspect
@RequiredArgsConstructor
public class LoggingAOP {

    private final LogTrace logTrace;
    private Logger log = LoggerFactory.getLogger("event");

    @Pointcut("execution(* com.mentoree..*Controller.*(..))")
    public void controllerLogger() {}

    @Pointcut("execution(* com.mentoree..*Service.*(..))")
    public void serviceLogger() {}

    @Pointcut("execution(* com.mentoree..*RepositoryImpl.*(..))")
    public void repositoryLogger() {}

    @Around("controllerLogger()")
    public Object controllerLog(ProceedingJoinPoint joinPoint) throws Throwable {
        logTrace.begin();
        TraceStatus currentTrace = logTrace.getCurrentTrace();

        LocalDateTime startTime = LocalDateTime.now();
        Object result = joinPoint.proceed();
        LocalDateTime endTime = LocalDateTime.now();

        Duration timeElapsed = Duration.between(startTime, endTime);


        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("current_level", currentTrace.getLevel());
        params.put("request_param", getParams(request));
        params.put("current_class", className);
        params.put("current_method", methodName);
        params.put("start_time", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        params.put("end_time", endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        params.put("elapsed_time(ms)", timeElapsed.toMillis());
        params.put("bottleneck", timeElapsed.toSeconds() > 5);

        log.info("[{}] : {}" , currentTrace.getId(), params);

        logTrace.complete();
        return result;
    }

    @Around("serviceLogger() || repositoryLogger()")
    public Object logicLog(ProceedingJoinPoint joinPoint) throws Throwable {
        logTrace.begin();
        TraceStatus currentTrace = logTrace.getCurrentTrace();

        LocalDateTime startTime = LocalDateTime.now();
        Object result = joinPoint.proceed();
        LocalDateTime endTime = LocalDateTime.now();

        Duration timeElapsed = Duration.between(startTime, endTime);

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("current_level", currentTrace.getLevel());
        params.put("current_class", className);
        params.put("current_method", methodName);
        params.put("start_time", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        params.put("end_time", endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        params.put("elapsed_time(ms)", timeElapsed.toMillis());
        params.put("bottleneck", timeElapsed.toSeconds() > 5);

        log.info("[{}] : {}" , currentTrace.getId(), params);

        logTrace.complete();
        return result;
    }

    @AfterThrowing(pointcut = "controllerLogger() || serviceLogger() || repositoryLogger()", throwing = "e")
    public void errorLog(JoinPoint joinPoint, Exception e) {
        logTrace.begin();
        TraceStatus currentTrace = logTrace.getCurrentTrace();

        String errorCauseClass = joinPoint.getSignature().getDeclaringType().getSimpleName();

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("Thrown_by", errorCauseClass);
        params.put("Exception", e);
        params.put("Message", e.getMessage());

        log.error("[{}] : {}", currentTrace.getId(), params);

        logTrace.error();
    }


    private static JSONObject getParams(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Enumeration<String> params = request.getParameterNames();
        while(params.hasMoreElements()) {
            String param  = params.nextElement();
            String replaceParam = param.replaceAll("\\.", "-");
            jsonObject.put(replaceParam, request.getParameter(param));
        }
        return jsonObject;
    }


}
