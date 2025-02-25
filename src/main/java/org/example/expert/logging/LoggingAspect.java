package org.example.expert.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public void pointCut() {
    }


    @Around("pointCut()")
    public Object loggingMethodCall(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        logger.info("===Admin Access: {}===", method.getName());
        return logReturn(joinPoint);
    }
    
    //로그 기록 위한 메서드
    private Object logReturn(ProceedingJoinPoint joinPoint) {
        String userId = String.valueOf(request.getAttribute("userId"));
        String requestUrl = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        //requestBody JSON -> String
        String requestBody = Arrays.stream(args)
                .map(arg -> {
                    try {
                        return objectMapper.writeValueAsString(arg);
                    } catch (Exception e) {
                        return "Failed to convert request body to JSON";
                    }
                })
                .toList()
                .toString();

        //실행
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            logger.error("[ERROR] {} - Message: {}", methodName, e.getMessage(), e);
            throw new RuntimeException(e);
        }

        //실행 후 결과 값인 responseBody JSON -> String
        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            responseBody = "Failed to convert response body to JSON";
        }

        logger.info("User Id: {}, Request Time: {}, Request URL: {}, RequestBody: {}, ResponseBody: {}", userId,
                requestTime,
                requestUrl, requestBody, responseBody);

        return result;
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
