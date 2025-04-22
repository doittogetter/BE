package com.doittogether.platform.common.config.interceptor;

import com.doittogether.platform.common.config.wrapper.CachedBodyHttpServletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        /**
         * remoteIp 가 IPv6 형태로 보인다면, IPv6 -> IPv4 로 vm 설정 옵션 추가 필요
         * -Djava.net.preferIPv4Stack=true
         * -Djava.net.preferIPv4Addresses=true
         */
        String remoteIp = request.getRemoteAddr(); // ip 정보
        String uri = request.getRequestURI(); // uri 정보
        String method = request.getMethod(); // method 정보
        Map<String, Object> parameters = new LinkedHashMap<>(); // 순차적으로 파라미터를 저장하기 위해 Linked 사용

        List<String> bodyMethods = List.of("POST", "PUT", "PATCH"); // Body 가 존재할 수 있는 메서드 목록

        if ("GET".equalsIgnoreCase(method)) { // GET 요청이면
            Map<String, String[]> paramMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                parameters.put(entry.getKey(), Arrays.asList(entry.getValue()));
            }
        } else if (bodyMethods.contains(method)) { // body 가 존재하는 요청들은 래핑된 request 에서 얻도록 한다.
            try {
                HttpServletRequest requestWrapper = new CachedBodyHttpServletRequest(request);
                String jsonBody = ((CachedBodyHttpServletRequest) requestWrapper).getCachedBodyAsString();

                if (!jsonBody.isBlank()) {
                    parameters.putAll(objectMapper.readValue(jsonBody, Map.class));
                }
            } catch (JsonProcessingException e) {
                // 만약, json 정보가 옳바르지 않을 경우, 사용자에게 JSON 형식 오류 메세지 전달 필요
                throw new HttpMessageNotReadableException("JSON 형식이 올바르지 않습니다.", e);
            } catch (IOException e) {
                // 예기치 못한 스트림 오류
                throw new IllegalStateException("요청 본문(body)을 읽는 도중 오류가 발생했습니다.", e);
            }
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new LinkedHashMap<>(); // 헤더 정보 매핑
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }

        log.info("Request: Remote_ip {}, Method: {}, URI: {}, Headers: {}, Parameters: {}",
                remoteIp, method, uri, headers, parameters);

        return true;
    }
}