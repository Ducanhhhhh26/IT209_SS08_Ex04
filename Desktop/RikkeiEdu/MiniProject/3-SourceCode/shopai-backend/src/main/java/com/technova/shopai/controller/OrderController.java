package com.technova.shopai.controller;

import com.technova.shopai.dto.OrderRequest;
import com.technova.shopai.dto.OrderResponse;
import com.technova.shopai.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Đặt hàng (Checkout) - yêu cầu đăng nhập gửi kèm JWT Token
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest orderRequest, Principal principal) {
        String username = principal.getName();
        OrderResponse orderResponse = orderService.placeOrder(orderRequest, username);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    // Xem lịch sử đơn hàng của khách hàng hiện tại
    @GetMapping("/history")
    public ResponseEntity<List<OrderResponse>> getOrderHistory(Principal principal) {
        String username = principal.getName();
        List<OrderResponse> history = orderService.getOrdersByUsername(username);
        return ResponseEntity.ok(history);
    }
}
