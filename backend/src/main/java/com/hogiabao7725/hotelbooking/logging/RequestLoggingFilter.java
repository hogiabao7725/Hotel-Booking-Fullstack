package com.hogiabao7725.hotelbooking.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_ID_MDC_KEY = "requestId";

    private static final int MAX_REQUEST_ID_LENGTH = 100;

    private static final Pattern VALID_REQUEST_ID_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._-]+$");

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String requestId = resolveRequestId(request);
        long startTime = System.nanoTime();
        boolean unhandledException = false;

        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException exception) {
            unhandledException = true;
            throw exception;
        } finally {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - startTime
            );

            int status = unhandledException
                    ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    : response.getStatus();

            logRequest(
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    durationMs
            );

            MDC.remove(REQUEST_ID_MDC_KEY);
        }
    }

    private void logRequest(String method, String path, int status, long durationMs) {
        if (status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            log.error(
                    "method={} path={} status={} duration={}ms",
                    method,
                    path,
                    status,
                    durationMs
            );
            return;
        }

        log.info(
                "method={} path={} status={} duration={}ms",
                method,
                path,
                status,
                durationMs
        );
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (isValidRequestId(requestId)) {
            return requestId;
        }
        return UUID.randomUUID().toString();
    }

    private boolean isValidRequestId(String requestId) {
        return StringUtils.hasText(requestId)
                && requestId.length() <= MAX_REQUEST_ID_LENGTH
                && VALID_REQUEST_ID_PATTERN.matcher(requestId).matches();
    }
}