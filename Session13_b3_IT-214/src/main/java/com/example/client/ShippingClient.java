package com.example.client;

import com.example.exception.ShippingServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class ShippingClient {

    @CircuitBreaker(name = "shippingClient")
    public double calculateShippingFee(double weight, boolean fail) {
        if (fail) {
            throw new ShippingServiceUnavailableException("Shipping-Service is currently down");
        }
        return weight * 5000.0;
    }
}
