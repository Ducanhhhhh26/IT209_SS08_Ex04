package com.technova.shopai.service.impl;

import com.technova.shopai.dto.OrderItemRequest;
import com.technova.shopai.dto.OrderItemResponse;
import com.technova.shopai.dto.OrderRequest;
import com.technova.shopai.dto.OrderResponse;
import com.technova.shopai.entity.Order;
import com.technova.shopai.entity.OrderItem;
import com.technova.shopai.entity.Product;
import com.technova.shopai.entity.User;
import com.technova.shopai.exception.InsufficientStockException;
import com.technova.shopai.exception.ResourceNotFoundException;
import com.technova.shopai.repository.OrderRepository;
import com.technova.shopai.repository.ProductRepository;
import com.technova.shopai.repository.UserRepository;
import com.technova.shopai.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PAID"); // Đơn hàng đã được thanh toán thành công
        order.setOrderItems(new ArrayList<>());

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            // Kiểm tra hàng tồn kho
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Product '" + product.getName() + "' is out of stock or does not have enough quantity. Available stock: " + product.getStockQuantity());
            }

            // Trừ hàng tồn kho
            int newStock = product.getStockQuantity() - itemRequest.getQuantity();
            product.setStockQuantity(newStock);
            if (newStock == 0) {
                product.setStatus("OUT_OF_STOCK");
            }
            productRepository.save(product);

            // Tính giá của item
            BigDecimal itemPrice = product.getPrice();
            BigDecimal subTotal = itemPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalPrice = totalPrice.add(subTotal);

            // Tạo OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(itemPrice)
                    .build();

            order.getOrderItems().add(orderItem);
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        // Gọi repository sử dụng @EntityGraph đã cấu hình để tải toàn bộ đơn hàng tránh lỗi N+1 Query
        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orders.stream().map(this::mapToOrderResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        // Tải toàn bộ đơn hàng kèm User & OrderItems tránh lỗi N+1 Query
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::mapToOrderResponse).collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream().map(item -> 
            OrderItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build()
        ).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .username(order.getUser().getUsername())
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .items(itemResponses)
                .build();
    }
}
