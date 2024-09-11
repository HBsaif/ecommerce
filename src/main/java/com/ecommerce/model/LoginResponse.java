package com.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

	private String accessToken;
	private String refreshToken;
	private long expiresIn;
	private boolean isFirstLogin;

}
