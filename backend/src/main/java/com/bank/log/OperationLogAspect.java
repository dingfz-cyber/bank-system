package com.bank.log;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作日志切面
 * 拦截 @OperationLog 注解，记录操作日志到本地文件
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint point, OperationLog operationLog) throws Throwable {
        String methodName = point.getSignature().toShortString();
        String operation = operationLog.value();
        String type = operationLog.type();

        Object result;
        boolean success = true;
        long start = System.currentTimeMillis();

        try {
            result = point.proceed();
            return result;
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            String operator = getOperator();
            String ip = getIp();
            String logMsg = String.format("[%s] [%s] [%s] 操作=%s 类型=%s 方法=%s 耗时=%dms 结果=%s IP=%s",
                    LocalDateTime.now().format(FORMATTER), operator, Thread.currentThread().getName(),
                    operation, type, methodName, duration, success ? "成功" : "失败", ip);
            log.info(logMsg);
        }
    }

    private String getOperator() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Object userId = request.getAttribute("userId");
            Object phone = request.getAttribute("phone");
            if (userId != null && phone != null) {
                return "userId=" + userId + "(" + phone + ")";
            }
            return "anonymous";
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String getIp() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            return ip;
        } catch (Exception e) {
            return "unknown";
        }
    }
}
