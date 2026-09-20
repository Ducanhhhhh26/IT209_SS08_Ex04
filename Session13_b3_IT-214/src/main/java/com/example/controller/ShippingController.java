package com.example.controller;

import com.example.client.ShippingClient;
import com.example.exception.ShippingServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    private final ShippingClient shippingClient;
    private final CircuitBreaker circuitBreaker;

    public ShippingController(ShippingClient shippingClient, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.shippingClient = shippingClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("shippingClient");
    }

    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateShippingFee(
            @RequestParam(defaultValue = "2.0") double weight,
            @RequestParam(defaultValue = "false") boolean fail) {
        double fee = shippingClient.calculateShippingFee(weight, fail);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("weight", weight);
        response.put("shippingFee", fee);
        response.put("circuitBreakerState", circuitBreaker.getState().name());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCircuitBreakerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("name", circuitBreaker.getName());
        status.put("state", circuitBreaker.getState().name());
        status.put("failureRate", circuitBreaker.getMetrics().getFailureRate());
        status.put("numberOfSuccessfulCalls", circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
        status.put("numberOfFailedCalls", circuitBreaker.getMetrics().getNumberOfFailedCalls());
        status.put("numberOfNotPermittedCalls", circuitBreaker.getMetrics().getNumberOfNotPermittedCalls());
        return ResponseEntity.ok(status);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Map<String, Object>> handleCallNotPermittedException(CallNotPermittedException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "REJECTED");
        error.put("exception", "CallNotPermittedException");
        error.put("message", ex.getMessage());
        error.put("circuitBreakerState", circuitBreaker.getState().name());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(ShippingServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleShippingServiceUnavailableException(ShippingServiceUnavailableException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "FAILED");
        error.put("exception", "ShippingServiceUnavailableException");
        error.put("message", ex.getMessage());
        error.put("circuitBreakerState", circuitBreaker.getState().name());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
