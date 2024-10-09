package com.ecommerce.product.dtos;

import lombok.Data;

@Data
public class UpdateItemQuantityRequest {
	
	private Integer userId;
    private Integer itemId;
    private Integer quantity;
}
