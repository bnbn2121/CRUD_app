package com.aston.homework.controller;

import com.aston.homework.service.CircuitBreakerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CircuitBreakerController {
    private final CircuitBreakerService circuitBreakerService;

    public CircuitBreakerController(CircuitBreakerService circuitBreakerService) {
        this.circuitBreakerService = circuitBreakerService;
    }

    @RequestMapping("/**")
    public Object handleAllRequests(HttpServletRequest request,
                                    @RequestBody(required = false) Object body) {
        return circuitBreakerService.forwardRequest(
                request.getRequestURI(),
                HttpMethod.valueOf(request.getMethod()),
                body
        );
    }
}
