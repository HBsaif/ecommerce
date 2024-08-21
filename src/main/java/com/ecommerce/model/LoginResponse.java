package com.ecommerce.model;

public class LoginResponse {

	private String token;
	private long expiresIn;
	boolean isFirstLogin;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	public boolean isFirstLogin() {
		return isFirstLogin;
	}

	public void setFirstLogin(boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

	public LoginResponse(String token, long expiresIn, boolean isFirstLogin) {
		super();
		this.token = token;
		this.expiresIn = expiresIn;
		this.isFirstLogin = isFirstLogin;
	}

}
