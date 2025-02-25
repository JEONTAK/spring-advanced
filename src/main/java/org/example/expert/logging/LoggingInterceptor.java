package org.example.expert.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object Handler) {
        LocalDateTime time = LocalDateTime.now();
        logger.info("===Admin Access: {}===)", request.getMethod());
        logger.info("Time: {}, URL: {}", time, request.getRequestURI());
        return true;
    }
}
