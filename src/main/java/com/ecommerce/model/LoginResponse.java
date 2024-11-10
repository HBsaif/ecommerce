package com.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class LoginResponse {

	private String accessToken;
	private String refreshToken;
	private long expiresIn;
	private boolean isFirstLogin;
	private int cartItems;

}
