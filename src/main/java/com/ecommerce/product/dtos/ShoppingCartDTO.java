package com.ecommerce.product.dtos;

import java.util.List;

import lombok.Data;

@Data
public class ShoppingCartDTO {
    private Long id;
    private Integer userId;
    private List<CartItemDTO> cartItems;

    // Getters and setters
}
