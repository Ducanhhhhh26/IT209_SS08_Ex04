package com.technova.shopai.service;

import com.technova.shopai.dto.OrderRequest;
import com.technova.shopai.dto.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest, String username);
    List<OrderResponse> getOrdersByUsername(String username);
    List<OrderResponse> getAllOrders();
}
