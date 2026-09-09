package com.technova.shopai.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String username;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private String status;
    private List<OrderItemResponse> items;
}
