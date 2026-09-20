package com.example;

import com.example.client.ShippingClient;
import com.example.exception.ShippingServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ShippingCircuitBreakerTest {

    @Autowired
    private ShippingClient shippingClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("shippingClient");
        circuitBreaker.reset();
    }

    @Test
    void testConfigurationMatchesSlaRequirements() {
        CircuitBreakerConfig config = circuitBreaker.getCircuitBreakerConfig();
        assertEquals(CircuitBreakerConfig.SlidingWindowType.TIME_BASED, config.getSlidingWindowType());
        assertEquals(30, config.getSlidingWindowSize());
        assertEquals(20000L, config.getWaitIntervalFunctionInOpenState().apply(1));
        assertEquals(3, config.getPermittedNumberOfCallsInHalfOpenState());
        assertTrue(config.isAutomaticTransitionFromOpenToHalfOpenEnabled());
    }

    @Test
    void testOpenToHalfOpenPermittedCallsLimitAndTransitionToClosed() {
        for (int i = 0; i < 3; i++) {
            assertThrows(ShippingServiceUnavailableException.class, () -> shippingClient.calculateShippingFee(2.0, true));
        }

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
        assertThrows(CallNotPermittedException.class, () -> shippingClient.calculateShippingFee(2.0, false));

        circuitBreaker.transitionToHalfOpenState();
        assertEquals(CircuitBreaker.State.HALF_OPEN, circuitBreaker.getState());

        AtomicInteger permittedCount = new AtomicInteger(0);
        AtomicInteger rejectedCount = new AtomicInteger(0);

        for (int i = 0; i < 10; i++) {
            try {
                circuitBreaker.acquirePermission();
                permittedCount.incrementAndGet();
            } catch (CallNotPermittedException e) {
                rejectedCount.incrementAndGet();
            }
        }

        assertEquals(3, permittedCount.get());
        assertEquals(7, rejectedCount.get());

        circuitBreaker.onSuccess(0, TimeUnit.NANOSECONDS);
        circuitBreaker.onSuccess(0, TimeUnit.NANOSECONDS);
        circuitBreaker.onSuccess(0, TimeUnit.NANOSECONDS);

        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }

    @Test
    void testHalfOpenTransitionToOpenOnFailure() {
        circuitBreaker.transitionToOpenState();
        circuitBreaker.transitionToHalfOpenState();
        assertEquals(CircuitBreaker.State.HALF_OPEN, circuitBreaker.getState());

        assertThrows(ShippingServiceUnavailableException.class, () -> shippingClient.calculateShippingFee(2.0, true));
        assertThrows(ShippingServiceUnavailableException.class, () -> shippingClient.calculateShippingFee(2.0, true));
        assertThrows(ShippingServiceUnavailableException.class, () -> shippingClient.calculateShippingFee(2.0, true));

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
    }

    @Test
    void testSuccessfulRecoveryViaClientCalls() {
        circuitBreaker.transitionToOpenState();
        circuitBreaker.transitionToHalfOpenState();
        assertEquals(CircuitBreaker.State.HALF_OPEN, circuitBreaker.getState());

        assertEquals(10000.0, shippingClient.calculateShippingFee(2.0, false));
        assertEquals(10000.0, shippingClient.calculateShippingFee(2.0, false));
        assertEquals(10000.0, shippingClient.calculateShippingFee(2.0, false));

        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }
}
