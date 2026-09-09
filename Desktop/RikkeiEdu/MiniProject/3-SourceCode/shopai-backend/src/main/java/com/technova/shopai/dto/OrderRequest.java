package com.technova.shopai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotEmpty(message = "Order items list cannot be empty")
    @Valid
    private List<OrderItemRequest> items;
}
