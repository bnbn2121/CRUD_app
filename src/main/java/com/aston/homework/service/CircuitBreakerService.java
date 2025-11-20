package com.aston.homework.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.function.Supplier;

@Service
public class CircuitBreakerService {

    @Value("${target.service.url}")
    private String serviceUrl;
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    public CircuitBreakerService(WebClient webClient, CircuitBreaker circuitBreaker) {
        this.webClient = webClient;
        this.circuitBreaker = circuitBreaker;
    }

    public Object forwardRequest(String path, HttpMethod method, Object body) {
        // Проверяем состояние Circuit Breaker перед вызовом
        if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
            return Map.of("Circuit Breaker warning", "Service unavailable");
        }

        // Создаем supplier для вызова сервиса
        Supplier<Object> serviceCall = () -> webClient.method(method)
                .uri(serviceUrl + path)
                .bodyValue(body != null ? body : "")
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        try {
            return circuitBreaker.executeSupplier(serviceCall);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private Object getOpenStateFallbackResponse() {
        return Map.of(
                "Circuit Breaker warning", "Service unavailable",
                "status", "circuit_breaker_open",
                "message", "Service is temporarily unavailable due to high error rate"
        );
    }

    private Object getExceptionFallbackResponse(Throwable throwable) {
        return Map.of(
                "error", "Service call failed",
                "message", "Unable to process request at this time"
        );
    }
}
