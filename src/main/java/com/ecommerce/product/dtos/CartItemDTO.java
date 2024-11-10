package com.ecommerce.product.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private BigDecimal priceAtTime;
    private Integer quantity;
    private String name;
    private String imageUrl;

    // Getters and setters
}
