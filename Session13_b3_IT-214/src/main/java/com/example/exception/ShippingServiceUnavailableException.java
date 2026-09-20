package com.example.exception;

public class ShippingServiceUnavailableException extends RuntimeException {
    public ShippingServiceUnavailableException(String message) {
        super(message);
    }
}
