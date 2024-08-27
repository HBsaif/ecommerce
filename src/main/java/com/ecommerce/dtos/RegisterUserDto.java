package com.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

	private String email;
	private String password;
	private String fullName;
	private String otp;
	private String requestId;
	
	
	
	@Override
	public String toString() {
		return "RegisterUserDto [email=" + email + ", fullName=" + fullName + "]";
	}



	public RegisterUserDto(String email, String password, String fullName) {
		super();
		this.email = email;
		this.password = password;
		this.fullName = fullName;
	}
	
	
}
