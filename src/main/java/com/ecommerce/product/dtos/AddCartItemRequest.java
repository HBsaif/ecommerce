package com.ecommerce.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AddCartItemRequest {
	@JsonProperty("userId")
	private Long userId;

	@JsonProperty("productId")
	private Long productId;

	@JsonProperty("quantity")
	private Integer quantity;
	// Getters and setters
}
