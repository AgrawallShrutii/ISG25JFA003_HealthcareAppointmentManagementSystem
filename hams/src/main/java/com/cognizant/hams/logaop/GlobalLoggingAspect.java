package com.cognizant.hams.logaop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
public class GlobalLoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalLoggingAspect.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Pointcut for all application methods
    @Pointcut("execution(* com.cognizant.hams..*(..)) && !within(com.cognizant.hams.security.JwtRequestFilter)")
    public void applicationPackagePointcut() {}

    // Pointcut for controller methods only
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    // Log request info only once per HTTP request (in controller)
    @Before("controllerMethods()")
    public void logRequestInfo(JoinPoint joinPoint) {
        String timestamp = LocalDateTime.now().format(formatter);
        HttpServletRequest request = getCurrentHttpRequest();

        LOGGER.info("==================================================");
        LOGGER.info("==========           NEW HIT         =============");
        LOGGER.info("==================================================");
        LOGGER.info("🕒 Timestamp: {}", timestamp);
        LOGGER.info("➡️ Endpoint Method: {} in class: {}", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getSimpleName());

        if (request != null) {
            LOGGER.info("🔗 Request URL: {}", request.getRequestURL());
            LOGGER.info("📨 HTTP Method: {}", request.getMethod());
            LOGGER.info("🌐 Client IP: {}", request.getRemoteAddr());
        }
    }

    // Log method entry for all methods
    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        LOGGER.info("➡️ Method {} is called in class {} ", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getSimpleName());
    }

    @After("applicationPackagePointcut()")
    public void logAfter(JoinPoint joinPoint) {
        LOGGER.info("✅ Method {} from class {} is completed", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getSimpleName());
    }

    @AfterReturning(pointcut = "applicationPackagePointcut()")
    public void logAfterReturning(JoinPoint joinPoint) {
        LOGGER.info("🎯 Method {} of class {} has returned", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getSimpleName());
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        LOGGER.error("❌ Method {} threw exception: {}", joinPoint.getSignature().getName(), ex.getMessage());
    }

    @Around("applicationPackagePointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        LOGGER.info("⏱️ Method {} of class {} executed in {} ms", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getSimpleName() , duration);
        return proceed;
    }

    private HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}